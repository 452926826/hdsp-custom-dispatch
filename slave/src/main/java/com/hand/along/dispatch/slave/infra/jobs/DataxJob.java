package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.infra.job.AbstractJob;
import com.hand.along.dispatch.common.infra.job.BaseJob;

public class DataxJob extends AbstractJob {
    private static final String JOB_TYPE = "datax";

    /**
     * 处理详情
     */
    @Override
    public void handle() {

    }

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

    }

    @Override
    public BaseJob getInstance() {
        return new DataxJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
