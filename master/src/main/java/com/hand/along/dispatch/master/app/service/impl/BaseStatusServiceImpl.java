package com.hand.along.dispatch.master.app.service.impl;

import com.hand.along.dispatch.common.app.service.AlertService;
import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.AlertInfo;
import com.hand.along.dispatch.common.domain.ExecutionLog;
import com.hand.along.dispatch.common.infra.mapper.ExecutionLogMapper;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.master.app.service.BaseStatusService;
import com.hand.along.dispatch.common.domain.WorkflowExecution;
import com.hand.along.dispatch.common.infra.mapper.WorkflowExecutionMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BaseStatusServiceImpl implements BaseStatusService {
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final ExecutionLogMapper executionLogMapper;
    private final AlertService alertService;

    public BaseStatusServiceImpl(WorkflowExecutionMapper workflowExecutionMapper,
                                 ExecutionLogMapper executionLogMapper,
                                 AlertService alertService) {
        this.workflowExecutionMapper = workflowExecutionMapper;
        this.executionLogMapper = executionLogMapper;
        this.alertService = alertService;
    }

    /**
     * 修改
     *
     * @param workflowExecution 执行
     * @param status            状态
     */
    @Override
    public void updateWorkflowExecutionStatus(WorkflowExecution workflowExecution, String status) {
        workflowExecution.setExecutionStatus(status);
        if(CommonConstant.ExecutionStatus.isFinished(status)){
            // todo:告警
            alertService.alert(AlertInfo.builder().build());
            workflowExecution.setEndDate(CommonUtil.now());
        }
        workflowExecutionMapper.updateByPrimaryKey(workflowExecution);
        ExecutionLog executionLog = workflowExecution.getExecutionLog();
        executionLog.setExecutionLog(StringUtils.join(workflowExecution.getLogs(), "\n"));
        executionLogMapper.updateByPrimaryKeyWithBLOBs(executionLog);
    }
}
