package com.hand.along.dispatch.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 描述：
 *
 * @author zhilong.deng@hand-china.com
 * @version 0.01
 */
public class CustomThreadPool {
    private CustomThreadPool() {
    }
    /**
     * 线程构造
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("dispatch-%d").setDaemon(true).build();

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10,
            20,
            10L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            THREAD_FACTORY,
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5, THREAD_FACTORY);


    /**
     * 获取线程池
     *
     * @return 线程池
     */
    public static ThreadPoolExecutor getExecutor() {
        return threadPoolExecutor;
    }

    public static ScheduledThreadPoolExecutor getScheduledExecutor(){
        return scheduledThreadPoolExecutor;
    }
}
