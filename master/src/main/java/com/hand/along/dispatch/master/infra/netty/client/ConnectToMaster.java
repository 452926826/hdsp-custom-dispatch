package com.hand.along.dispatch.master.infra.netty.client;

import com.hand.along.dispatch.common.utils.CustomThreadPool;
import com.hand.along.dispatch.common.utils.RedisHelper;
import com.hand.along.dispatch.master.infra.election.CurrentMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hand.along.dispatch.common.constants.CommonConstant.MASTER_LOCK;

@Component
@Slf4j
public class ConnectToMaster {
    private final RedisHelper redisHelper;
    private final ThreadPoolExecutor threadPoolExecutor = CustomThreadPool.getExecutor();
    private final CurrentMasterService currentMasterService;

    public ConnectToMaster(RedisHelper redisHelper,
                           CurrentMasterService currentMasterService) {
        this.redisHelper = redisHelper;
        this.currentMasterService = currentMasterService;
    }

    /**
     * 5s检测一次是否主节点已经更改
     */
    @PostConstruct
    public void init() {
        String currentIp = currentMasterService.getCurrentIp();
        threadPoolExecutor.submit(() -> {
            while (true) {
                try {
                    if (redisHelper.hasKey(MASTER_LOCK)) {
                        String masterUrl = redisHelper.strGet(MASTER_LOCK);
                        if (!masterUrl.equals(currentIp)) {
                            String[] split = masterUrl.split(":");
                            //启动netty客户端
                            NettyClient nettyClient = new NettyClient();
                            log.info("连接到主节点");
                            // 连接上server后会阻塞
                            nettyClient.start(split[0], Integer.valueOf(split[1]));
                        }else{
                            log.info("选举出来的主节点就是当前节点，不做其他操作");
                        }
                    } else {
                        log.error("主节点没有选举出来，等待主节点选举");
                        TimeUnit.SECONDS.sleep(5L);
                    }
                } catch (Exception e) {
                    log.error("error", e);
                }
            }
        });
    }
}
