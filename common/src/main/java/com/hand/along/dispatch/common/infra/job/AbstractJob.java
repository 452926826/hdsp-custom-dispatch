package com.hand.along.dispatch.common.infra.job;

import com.hand.along.dispatch.common.domain.JobNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class AbstractJob implements BaseJob {
    private Map<String, Object> localParams;
    private JobNode jobNode;

    public AbstractJob() {

    }

    /**
     * 执行
     */
    @Override
    public void run() {
        before();
        handle();
        after();
    }

    /**
     * 处理详情
     */
    public abstract void handle();

    /**
     * 处理之前
     */
    public abstract void before();

    /**
     * 处理之后
     */
    public abstract void after();

    public abstract BaseJob getInstance();

    public abstract String getJobType();

    public abstract void cancel();

    public Map<String, Object> getLocalParams() {
        return localParams;
    }

    public void setLocalParams(Map<String, Object> params) {
        this.localParams = params;
    }

    public JobNode getJobNode() {
        return jobNode;
    }

    public void setJobNode(JobNode jobNode) {
        this.jobNode = jobNode;
    }
}
