package com.hand.along.dispatch.master.app.service;

import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.master.domain.WorkflowExecution;

public interface WorkflowService {
    /**
     * 执行任务流
     *
     * @param workflow 任务流
     * @return 返回执行记录
     */
    WorkflowExecution execute(Workflow workflow);

    /**
     * 加载未完成的任务流
     */
    void loadUnfinishedWorkflow();

    /**
     * 调度计划
     * @param workflow workflow
     */
    void cron(Workflow workflow);
}
