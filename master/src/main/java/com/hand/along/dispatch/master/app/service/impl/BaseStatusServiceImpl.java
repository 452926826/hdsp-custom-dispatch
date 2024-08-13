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
import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.master.infra.mapper.WorkflowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BaseStatusServiceImpl implements BaseStatusService {
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final ExecutionLogMapper executionLogMapper;
    private final AlertService alertService;
    private final WorkflowMapper workflowMapper;

    public BaseStatusServiceImpl(WorkflowExecutionMapper workflowExecutionMapper,
                                 ExecutionLogMapper executionLogMapper,
                                 AlertService alertService,
                                 WorkflowMapper workflowMapper) {
        this.workflowExecutionMapper = workflowExecutionMapper;
        this.executionLogMapper = executionLogMapper;
        this.alertService = alertService;
        this.workflowMapper = workflowMapper;
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
        String log = StringUtils.join(workflowExecution.getLogs(), "\n");
        if (CommonConstant.ExecutionStatus.isFinished(status)) {
            Workflow workflow = workflowMapper.selectByPrimaryKey(workflowExecution.getWorkflowId());
            // todo:告警
            alertService.alert(AlertInfo.builder()
                    .alertDate(CommonUtil.now())
                    .alertObject(CommonConstant.WORKFLOW)
                    .workflowName(workflow.getWorkflowName())
                    .alertType(CommonConstant.ExecutionStatus.isSuccess(status) ? CommonConstant.ALERT_SUCCESS : CommonConstant.ALERT_FAIL)
                    .mailList("")
                    .subject("DISPATCH ALERT")
                    .log(log)
                    .build());
            workflowExecution.setEndDate(CommonUtil.now());
        }
        workflowExecutionMapper.updateByPrimaryKey(workflowExecution);
        ExecutionLog executionLog = workflowExecution.getExecutionLog();
        executionLog.setExecutionLog(log);
        executionLogMapper.updateByPrimaryKeyWithBLOBs(executionLog);
    }
}
