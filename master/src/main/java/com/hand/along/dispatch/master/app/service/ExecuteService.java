package com.hand.along.dispatch.master.app.service;

import com.hand.along.dispatch.common.domain.ExecutorInfo;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.common.domain.WorkflowExecution;

import java.util.List;
import java.util.Map;

/**
 * 执行服务
 */
public interface ExecuteService {
    /**
     * 获取执行线程池信息
     *
     * @return 线程池信息
     */
    ExecutorInfo getExecutorInfo();

    /**
     * 提交任务流
     *
     * @param workflow          任务流
     * @param tmpNodeMap        临时节点存储
     * @param sourceList        开始节点
     * @param workflowExecution 执行记录
     */
    void submitWorkflow(Workflow workflow, Map<String, JobNode> tmpNodeMap, List<String> sourceList, WorkflowExecution workflowExecution);
}
