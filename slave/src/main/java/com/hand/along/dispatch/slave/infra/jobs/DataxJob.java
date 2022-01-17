package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.slave.infra.jobs.process.AbstractProcessJob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataxJob extends AbstractProcessJob {
    private static final String JOB_TYPE = "datax";

    /**
     * 处理详情
     */
    @Override
    public void handle() {
      log.info("this is datax!");
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
