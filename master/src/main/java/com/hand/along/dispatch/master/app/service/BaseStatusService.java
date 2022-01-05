package com.hand.along.dispatch.master.app.service;

import com.hand.along.dispatch.master.domain.WorkflowExecution;

public interface BaseStatusService {
    /**
     * 修改
     *
     * @param workflowExecution 执行
     * @param status            状态
     */
    void updateWorkflowExecutionStatus(WorkflowExecution workflowExecution, String status);
}
