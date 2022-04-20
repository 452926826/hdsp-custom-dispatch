package com.hand.along.dispatch.common.infra.job;


public interface BaseJob extends Runnable {

    /**
     * 执行
     */
    @Override
    void run();

    String getJobType();

    void cancel();
}
