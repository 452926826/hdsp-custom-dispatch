package com.hand.along.dispatch.slave.infra.netty;

import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.CustomThreadPool;
import com.hand.along.dispatch.common.utils.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.hand.along.dispatch.common.constants.CommonConstant.MASTER_LOCK;

@Component
@Slf4j
public class ConnectToServer {
    private final RedisHelper redisHelper;
    private final ThreadPoolExecutor threadPoolExecutor = CustomThreadPool.getExecutor();
    private Object lock = new Object();

    public ConnectToServer(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 5s检测一次是否master已经更改
     */
    @PostConstruct
    public void init() {
        threadPoolExecutor.submit(() -> {
            while (true) {
                try {
                    if (redisHelper.hasKey(MASTER_LOCK)) {
                        String masterUrl = redisHelper.strGet(MASTER_LOCK);
                        String[] split = masterUrl.split(":");
                        //启动netty客户端
                        NettyClient nettyClient = new NettyClient();
                        log.info("连接master");
                        // 连接上server后会阻塞
                        nettyClient.start(split[0], Integer.valueOf(split[1]));
                    } else {
                        log.error("master没有启动，请启动一个master！！！");
                        TimeUnit.SECONDS.sleep(5L);
                    }
                } catch (Exception e) {
                    log.error("error", e);
                }
            }
        });
    }
}
