package com.hand.along.dispatch.master.app.service.impl;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.ExecutionLog;
import com.hand.along.dispatch.common.domain.ExecutionLogExample;
import com.hand.along.dispatch.common.domain.JobExecution;
import com.hand.along.dispatch.common.domain.JobExecutionExample;
import com.hand.along.dispatch.common.infra.mapper.ExecutionLogMapper;
import com.hand.along.dispatch.common.infra.mapper.JobExecutionMapper;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.master.app.service.BaseStatusService;
import com.hand.along.dispatch.master.app.service.WorkflowService;
import com.hand.along.dispatch.master.domain.*;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.master.infra.handler.GraphUtil;
import com.hand.along.dispatch.master.infra.mapper.WorkflowExecutionMapper;
import com.hand.along.dispatch.master.infra.mapper.WorkflowMapper;
import com.hand.along.dispatch.master.infra.quartz.QuartzHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

    public WorkflowServiceImpl(WorkflowMapper workflowMapper,
                               ExecutionLogMapper executionLogMapper,
                               GraphUtil graphUtil,
                               WorkflowExecutionMapper workflowExecutionMapper,
                               BaseStatusService baseStatusService,
                               JobExecutionMapper jobExecutionMapper,
                               QuartzHandler quartzHandler) {
        this.workflowMapper = workflowMapper;
        this.executionLogMapper = executionLogMapper;
        this.graphUtil = graphUtil;
        this.workflowExecutionMapper = workflowExecutionMapper;
        this.baseStatusService = baseStatusService;
        this.jobExecutionMapper = jobExecutionMapper;
        this.quartzHandler = quartzHandler;
    }

    /**
     * 执行任务流
     *
     * @param workflow 任务流
     * @return 返回执行记录
     */
    @Override
    public WorkflowExecution execute(Workflow workflow) {
        String graph = workflow.getGraph();
        if (StringUtils.isEmpty(graph)) {
            throw new CommonException("任务流图形为空");
        }
        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .workflowId(workflow.getWorkflowId())
                .startDate(CommonUtil.now())
                .workflowGraph(graph)
                .executionStatus(CommonConstant.ExecutionStatus.READY.name())
                .build();
        workflowExecutionMapper.insertSelective(workflowExecution);
        ExecutionLog executionLog = ExecutionLog.builder()
                .executionId(workflowExecution.getWorkflowExecutionId())
                .executionType(CommonConstant.NodeType.WORKFLOW.name())
                .build();
        executionLogMapper.insert(executionLog);
        // 关联的日志记录
        workflowExecution.setExecutionLog(executionLog);
        graphUtil.parseGraph(graph, workflow, workflowExecution, Collections.EMPTY_LIST);
        return workflowExecution;
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
                // 获取任务的历史记录
                JobExecutionExample jobExecutionExample = new JobExecutionExample();
                jobExecutionExample.createCriteria().andExecutionIdEqualTo(e.getWorkflowExecutionId());
                List<JobExecution> jobExecutionList = jobExecutionMapper.selectByExample(jobExecutionExample);
                graphUtil.parseGraph(workflowGraph, workflow, e, jobExecutionList);
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
                .cronExpression("0 0/5 * * * ? *")
                .graph(workflow.getGraph())
                .build());
    }
}
