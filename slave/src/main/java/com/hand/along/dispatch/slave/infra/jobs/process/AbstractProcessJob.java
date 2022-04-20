package com.hand.along.dispatch.slave.infra.jobs.process;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化几个mapper
 */
@Slf4j
public abstract class AbstractProcessJob extends AbstractCommonJob {


    /**
     * 处理详情
     */
    @Override
    public abstract void handle();

    /**
     * 处理之后  所有步骤执行成功后 指定状态为SUCCESS
     */
    @Override
    public void after() {
        JobNode jobNode = getJobNode();
        log.info("任务执行成功:{}", jobNode.getId());
        jobNode.setStatus(CommonConstant.ExecutionStatus.SUCCEEDED.name());
        jobNode.setEndDate(CommonUtil.now());
        NettyClient.sendMessage(JSON.toJson(jobNode));
        jobExecution.setExecutionStatus(CommonConstant.ExecutionStatus.SUCCEEDED.name());
        jobExecutionMapper.updateByPrimaryKey(jobExecution);
        jobExecution.info("任务执行成功:{}", jobNode.getId());
        updateJobLog();
    }

    @Override
    public void cancel() {

    }
}
