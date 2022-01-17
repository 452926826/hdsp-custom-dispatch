package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.domain.WorkflowExecution;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.infra.mapper.WorkflowExecutionMapper;
import com.hand.along.dispatch.common.utils.ApplicationHelper;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.slave.infra.handler.JobHandler;
import com.hand.along.dispatch.slave.infra.jobs.process.AbstractProcessJob;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 子任务流执行
 */
@Slf4j
public class SubWorkflowJob extends AbstractProcessJob {
    // 子任务流默认超时时间
    private static final Long defaultTimeout = 3600L * 1000;
    private static final String JOB_TYPE = "sub-workflow";
    private static WorkflowExecutionMapper workflowExecutionMapper;

    /**
     * 处理详情
     */
    @Override
    public void handle() {
        if(Objects.isNull(workflowExecutionMapper)){
            workflowExecutionMapper = ApplicationHelper.getBean(WorkflowExecutionMapper.class);
        }
        log.info("this is sub-workflow!");
        JobNode jobNode = getJobNode();
        jobNode.setMessageType(CommonConstant.EXECUTE_SUB_WORKFLOW);
        String uuid = UUID.randomUUID().toString();
        jobNode.setUuid(uuid);
        NettyClient.sendMessage(JSON.toJson(jobNode));
        AtomicBoolean flag = new AtomicBoolean(true);
        long start = System.currentTimeMillis();
        while (flag.get()) {
            long end = System.currentTimeMillis();
            if ((end - start) > defaultTimeout) {
                flag.set(false);
                throw new CommonException("子任务流执行超时！");
            } else {
                JobNode subWorkflow = JobHandler.getSubWorkflow(uuid);
                if (Objects.nonNull(subWorkflow)) {
                    WorkflowExecution workflowExecution = workflowExecutionMapper.selectByPrimaryKey(subWorkflow.getExecutionId());
                    String executionStatus = workflowExecution.getExecutionStatus();
                    if (CommonConstant.ExecutionStatus.isFailed(executionStatus)) {
                        flag.set(false);
                        // 重要：把消息类型设置为job
                        jobNode.setMessageType(CommonConstant.JOB);
                        throw new CommonException("子任务流执行失败");
                    }
                    if (CommonConstant.ExecutionStatus.isFinished(executionStatus)) {
                        log.info("子任务流执行完成");
                        // 重要：把消息类型设置为job
                        jobNode.setMessageType(CommonConstant.JOB);
                        flag.set(false);
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public BaseJob getInstance() {
        return new SubWorkflowJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
