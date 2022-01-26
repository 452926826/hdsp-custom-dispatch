package com.hand.along.dispatch.master.app.service.impl;

import com.hand.along.dispatch.common.domain.ExecutorInfo;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.utils.CustomThreadPool;
import com.hand.along.dispatch.master.app.service.BaseStatusService;
import com.hand.along.dispatch.master.app.service.ExecuteService;
import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.common.domain.WorkflowExecution;
import com.hand.along.dispatch.master.infra.handler.WorkflowTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 执行任务流服务
 */
@Service
@Slf4j
public class ExecuteServiceImpl implements ExecuteService {
    private static final ThreadPoolExecutor EXECUTOR = CustomThreadPool.getExecutor();
    private static final Map<Long, Future<?>> FUTURES = new ConcurrentHashMap<>();
    private final BaseStatusService baseStatusService;

    public ExecuteServiceImpl(BaseStatusService baseStatusService) {
        this.baseStatusService = baseStatusService;
    }

    /**
     * 获取执行线程池信息
     *
     * @return 线程池信息
     */
    @Override
    public ExecutorInfo getExecutorInfo() {
        return ExecutorInfo.builder()
                .activeCount(EXECUTOR.getActiveCount())
                .completedTaskCount(EXECUTOR.getCompletedTaskCount())
                .taskCount(EXECUTOR.getTaskCount())
                .corePoolSize(EXECUTOR.getCorePoolSize())
                .maximumPoolSize(EXECUTOR.getMaximumPoolSize())
                .queueCount(EXECUTOR.getQueue().size())
                .build();
    }


    /**
     * 提交任务流
     *
     * @param workflow          任务流
     * @param tmpNodeMap        临时节点存储
     * @param sourceList        开始节点
     * @param workflowExecution 执行记录
     */
    @Override
    public void submitWorkflow(Workflow workflow, Map<String, JobNode> tmpNodeMap, List<String> sourceList, WorkflowExecution workflowExecution) {
        log.info("提交任务流");
        workflowExecution.info("提交任务流");
        final Future<?> future = EXECUTOR.submit(new WorkflowTask(workflow,
                sourceList,
                tmpNodeMap,
                baseStatusService,
                workflowExecution));
        FUTURES.put(workflowExecution.getWorkflowExecutionId(), future);
    }
}
