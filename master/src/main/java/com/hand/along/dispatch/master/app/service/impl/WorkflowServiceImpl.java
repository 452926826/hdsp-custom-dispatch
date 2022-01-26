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
     * 执行任务流
     *
     * @param workflow 任务流
     */
    @Override
    public WorkflowExecution execute(Workflow workflow) {
        // 如果当前节点是主节点则直接执行
        if (ElectionConfiguration.isMaster) {
            String graph = workflow.getGraph();
            if (StringUtils.isEmpty(graph)) {
                throw new CommonException("任务流图形为空");
            }
            // 全局参数
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
            // 关联的日志记录
            workflowExecution.setExecutionLog(executionLog);
            final List<String> sourceList = new ArrayList<>();
            final List<String> targetList = new ArrayList<>();
            Map<String, JobNode> tmpNodeMap = graphUtil.parseGraph(graph, workflow, workflowExecution, sourceList, targetList, Collections.EMPTY_LIST);
            // 提交任务流
            executeService.submitWorkflow(workflow, tmpNodeMap, sourceList, workflowExecution);
            return workflowExecution;
        } else {
            //如果当前节点不是主节点则发送到主节点执行
            workflow.setMessageType(EXECUTE_WORKFLOW);
            NettyClient.sendMessage(JSON.toJson(workflow));
        }
        return WorkflowExecution.builder().build();
    }

    /**
     * 准备全局参数
     *
     * @param globalParamMap 全局参数
     */
    private void prepareGlobalParam(Map<String, Object> globalParamMap) {
        globalParamMap.put(CommonConstant.GLOBALPARAM.G_CURRENT_DATE_TIME.getValue(),CommonUtil.formatNow());
    }

    /**
     * 加载未完成的任务流
     */
    @Override
    public void loadUnfinishedWorkflow() {
        WorkflowExecutionExample executionExample = new WorkflowExecutionExample();
        executionExample.createCriteria()
                .andExecutionStatusEqualTo(CommonConstant.ExecutionStatus.RUNNING.name());
        List<WorkflowExecution> workflowExecutionList = workflowExecutionMapper.selectByExampleWithBLOBs(executionExample);
        log.info("加载没有运行完的任务流，数量有：{}条", workflowExecutionList.size());
        workflowExecutionList.forEach(e -> {
            // 获取日志记录
            ExecutionLogExample logExample = new ExecutionLogExample();
            logExample.createCriteria().andExecutionIdEqualTo(e.getWorkflowExecutionId())
                    .andExecutionTypeEqualTo(CommonConstant.NodeType.WORKFLOW.name());
            List<ExecutionLog> executionLogs = executionLogMapper.selectByExampleWithBLOBs(logExample);
            if (CollectionUtils.isNotEmpty(executionLogs)) {
                // 如果日志记录非空
                e.setExecutionLog(executionLogs.get(0));
            } else {
                // 如果日志记录为空
                ExecutionLog executionLog = ExecutionLog.builder()
                        .executionId(e.getWorkflowExecutionId())
                        .executionType(CommonConstant.NodeType.WORKFLOW.name())
                        .build();
                executionLogMapper.insert(executionLog);
                e.setExecutionLog(executionLog);
            }
            // 获取任务流详情
            WorkflowExample workflowExample = new WorkflowExample();
            workflowExample.createCriteria().andWorkflowIdEqualTo(e.getWorkflowId());
            List<Workflow> workflows = workflowMapper.selectByExample(workflowExample);
            if (CollectionUtils.isNotEmpty(workflows)) {
                Workflow workflow = workflows.get(0);
                // 使用任务流记录中的graph替换workflow中的graph
                String workflowGraph = e.getWorkflowGraph();
                workflow.setGraph(workflowGraph);
                // 加载任务流执行参数
                if (StringUtils.isNotEmpty(e.getExecutionParam())) {
                    workflow.setParamMap((Map<String, Object>) JSON.toObj(e.getExecutionParam(), Map.class));
                }
                // 获取任务的历史记录
                JobExecutionExample jobExecutionExample = new JobExecutionExample();
                jobExecutionExample.createCriteria().andExecutionIdEqualTo(e.getWorkflowExecutionId());
                List<JobExecution> jobExecutionList = jobExecutionMapper.selectByExample(jobExecutionExample);
                final List<String> sourceList = new ArrayList<>();
                final List<String> targetList = new ArrayList<>();
                Map<String, JobNode> tmpNodeMap = graphUtil.parseGraph(workflowGraph, workflow, e, sourceList, targetList, jobExecutionList);
                // 提交任务流
                executeService.submitWorkflow(workflow, tmpNodeMap, sourceList, e);
            } else {
                log.warn("当前正在运行的执行记录找不到原始任务流！直接停掉");
                e.warn("当前正在运行的执行记录找不到原始任务流！直接停掉");
                baseStatusService.updateWorkflowExecutionStatus(e, CommonConstant.ExecutionStatus.FAILED.name());
            }
        });
    }

    /**
     * 调度计划
     *
     * @param workflow workflow
     */
    @Override
    public void cron(Workflow workflow) {
        quartzHandler.startCron(WorkflowSchedule.builder()
                .workflowId(workflow.getWorkflowId())
                .workflowName(workflow.getWorkflowName())
                .workflowCode(workflow.getWorkflowCode())
                // 等待传值
                .cronExpression("0 0/5 * * * ? *")
                .graph(workflow.getGraph())
                .build());
    }
}
