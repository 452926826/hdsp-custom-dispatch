package com.hand.along.dispatch.common.infra.job;

import java.util.Map;

public abstract class AbstractJob implements BaseJob {
    private Map<String,Object> params;

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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
