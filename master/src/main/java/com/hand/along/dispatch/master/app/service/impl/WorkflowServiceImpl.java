package com.hand.along.dispatch.master.app.service.impl;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.*;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.mapper.ExecutionLogMapper;
import com.hand.along.dispatch.common.infra.mapper.JobExecutionMapper;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.master.app.service.BaseStatusService;
import com.hand.along.dispatch.master.app.service.ExecuteService;
import com.hand.along.dispatch.master.app.service.WorkflowService;
import com.hand.along.dispatch.master.domain.*;
import com.hand.along.dispatch.master.infra.election.ElectionConfiguration;
import com.hand.along.dispatch.master.infra.handler.GraphUtil;
import com.hand.along.dispatch.common.infra.mapper.WorkflowExecutionMapper;
import com.hand.along.dispatch.master.infra.mapper.WorkflowMapper;
import com.hand.along.dispatch.master.infra.netty.client.NettyClient;
import com.hand.along.dispatch.master.infra.quartz.QuartzHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.hand.along.dispatch.common.constants.CommonConstant.EXECUTE_WORKFLOW;

@Service
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {
    private final WorkflowMapper workflowMapper;
    private final ExecutionLogMapper executionLogMapper;
    private final GraphUtil graphUtil;
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final BaseStatusService baseStatusService;
    private final JobExecutionMapper jobExecutionMapper;
    private final QuartzHandler quartzHandler;
    private final ExecuteService executeService;

    public WorkflowServiceImpl(WorkflowMapper workflowMapper,
                               ExecutionLogMapper executionLogMapper,
                               GraphUtil graphUtil,
                               WorkflowExecutionMapper workflowExecutionMapper,
                               BaseStatusService baseStatusService,
                               JobExecutionMapper jobExecutionMapper,
                               QuartzHandler quartzHandler,
                               ExecuteService executeService) {
        this.workflowMapper = workflowMapper;
        this.executionLogMapper = executionLogMapper;
        this.graphUtil = graphUtil;
        this.workflowExecutionMapper = workflowExecutionMapper;
        this.baseStatusService = baseStatusService;
        this.jobExecutionMapper = jobExecutionMapper;
        this.quartzHandler = quartzHandler;
        this.executeService = executeService;
    }

    /**
     * ???????????????
     *
     * @param workflow ?????????
     */
    @Override
    public WorkflowExecution execute(Workflow workflow) {
        // ?????????????????????????????????????????????
        if (ElectionConfiguration.isMaster) {
            String graph = workflow.getGraph();
            if (StringUtils.isEmpty(graph)) {
                throw new CommonException("?????????????????????");
            }
            // ????????????
            Map<String, Object> globalParamMap = new HashMap<>(16);
            prepareGlobalParam(globalParamMap);
            WorkflowExecution workflowExecution = WorkflowExecution.builder()
                    .workflowId(workflow.getWorkflowId())
                    .startDate(CommonUtil.now())
                    .workflowGraph(graph)
                    .executionParam(JSON.toJson(globalParamMap))
                    .executionStatus(CommonConstant.ExecutionStatus.READY.name())
                    .build();
            workflowExecutionMapper.insertSelective(workflowExecution);
            ExecutionLog executionLog = ExecutionLog.builder()
                    .executionId(workflowExecution.getWorkflowExecutionId())
                    .executionType(CommonConstant.NodeType.WORKFLOW.name())
                    .build();
            executionLogMapper.insert(executionLog);
            workflow.setParamMap(globalParamMap);
            // ?????????????????????
            workflowExecution.setExecutionLog(executionLog);
            final List<String> sourceList = new ArrayList<>();
            final List<String> targetList = new ArrayList<>();
            Map<String, JobNode> tmpNodeMap = graphUtil.parseGraph(graph, workflow, workflowExecution, sourceList, targetList, Collections.EMPTY_LIST);
            // ???????????????
            executeService.submitWorkflow(workflow, tmpNodeMap, sourceList, workflowExecution);
            return workflowExecution;
        } else {
            //????????????????????????????????????????????????????????????
            workflow.setMessageType(EXECUTE_WORKFLOW);
            NettyClient.sendMessage(JSON.toJson(workflow));
        }
        return WorkflowExecution.builder().build();
    }

    /**
     * ??????????????????
     *
     * @param globalParamMap ????????????
     */
    private void prepareGlobalParam(Map<String, Object> globalParamMap) {
        globalParamMap.put(CommonConstant.GLOBALPARAM.G_CURRENT_DATE_TIME.getValue(),CommonUtil.formatNow());
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void loadUnfinishedWorkflow() {
        WorkflowExecutionExample executionExample = new WorkflowExecutionExample();
        executionExample.createCriteria()
                .andExecutionStatusEqualTo(CommonConstant.ExecutionStatus.RUNNING.name());
        List<WorkflowExecution> workflowExecutionList = workflowExecutionMapper.selectByExampleWithBLOBs(executionExample);
        log.info("????????????????????????????????????????????????{}???", workflowExecutionList.size());
        workflowExecutionList.forEach(e -> {
            // ??????????????????
            ExecutionLogExample logExample = new ExecutionLogExample();
            logExample.createCriteria().andExecutionIdEqualTo(e.getWorkflowExecutionId())
                    .andExecutionTypeEqualTo(CommonConstant.NodeType.WORKFLOW.name());
            List<ExecutionLog> executionLogs = executionLogMapper.selectByExampleWithBLOBs(logExample);
            if (CollectionUtils.isNotEmpty(executionLogs)) {
                // ????????????????????????
                e.setExecutionLog(executionLogs.get(0));
            } else {
                // ????????????????????????
                ExecutionLog executionLog = ExecutionLog.builder()
                        .executionId(e.getWorkflowExecutionId())
                        .executionType(CommonConstant.NodeType.WORKFLOW.name())
                        .build();
                executionLogMapper.insert(executionLog);
                e.setExecutionLog(executionLog);
            }
            // ?????????????????????
            WorkflowExample workflowExample = new WorkflowExample();
            workflowExample.createCriteria().andWorkflowIdEqualTo(e.getWorkflowId());
            List<Workflow> workflows = workflowMapper.selectByExample(workflowExample);
            if (CollectionUtils.isNotEmpty(workflows)) {
                Workflow workflow = workflows.get(0);
                // ???????????????????????????graph??????workflow??????graph
                String workflowGraph = e.getWorkflowGraph();
                workflow.setGraph(workflowGraph);
                // ???????????????????????????
                if (StringUtils.isNotEmpty(e.getExecutionParam())) {
                    workflow.setParamMap((Map<String, Object>) JSON.toObj(e.getExecutionParam(), Map.class));
                }
                // ???????????????????????????
                JobExecutionExample jobExecutionExample = new JobExecutionExample();
                jobExecutionExample.createCriteria().andExecutionIdEqualTo(e.getWorkflowExecutionId());
                List<JobExecution> jobExecutionList = jobExecutionMapper.selectByExample(jobExecutionExample);
                final List<String> sourceList = new ArrayList<>();
                final List<String> targetList = new ArrayList<>();
                Map<String, JobNode> tmpNodeMap = graphUtil.parseGraph(workflowGraph, workflow, e, sourceList, targetList, jobExecutionList);
                // ???????????????
                executeService.submitWorkflow(workflow, tmpNodeMap, sourceList, e);
            } else {
                log.warn("????????????????????????????????????????????????????????????????????????");
                e.warn("????????????????????????????????????????????????????????????????????????");
                baseStatusService.updateWorkflowExecutionStatus(e, CommonConstant.ExecutionStatus.FAILED.name());
            }
        });
    }

    /**
     * ????????????
     *
     * @param workflow workflow
     */
    @Override
    public void cron(Workflow workflow) {
        quartzHandler.startCron(WorkflowSchedule.builder()
                .workflowId(workflow.getWorkflowId())
                .workflowName(workflow.getWorkflowName())
                .workflowCode(workflow.getWorkflowCode())
                // ????????????
                .cronExpression("0 0/5 * * * ? *")
                .graph(workflow.getGraph())
                .build());
    }
}
