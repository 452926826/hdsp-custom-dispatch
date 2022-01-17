package com.hand.along.dispatch.slave.infra.jobs.process;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.infra.job.AbstractJob;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractProcessJob extends AbstractJob {
    /**
     * 处理详情
     */
    @Override
    public abstract void handle();

    /**
     * 处理之前
     */
    @Override
    public void before() {

    }

    /**
     * 处理之后
     */
    @Override
    public void after() {
        JobNode jobNode = getJobNode();
        log.info("任务执行成功:{}", jobNode.getId());
        jobNode.setStatus(CommonConstant.ExecutionStatus.SUCCEEDED.name());
        jobNode.setEndDate(CommonUtil.now());
        NettyClient.sendMessage(JSON.toJson(jobNode));
    }

    @Override
    public abstract BaseJob getInstance();

    @Override
    public abstract String getJobType();
}
