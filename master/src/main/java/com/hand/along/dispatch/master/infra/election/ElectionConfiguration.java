package com.hand.along.dispatch.master.infra.election;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.monitor.MasterMonitorInfo;
import com.hand.along.dispatch.common.utils.CustomThreadPool;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.common.utils.RedisHelper;
import com.hand.along.dispatch.master.app.service.ExecuteService;
import com.hand.along.dispatch.master.app.service.WorkflowService;
import com.hand.along.dispatch.master.infra.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.hand.along.dispatch.common.constants.CommonConstant.MASTER_LOCK;

@Component
@Slf4j
public class ElectionConfiguration {
    private final RedisLockRegistry redisLockRegistry;
    private final WorkflowService workflowService;
    private final RedisHelper redisHelper;
    private final CurrentMasterService currentMasterService;
    private final ExecuteService executeService;

    private static final String MASTER_ELECTION_LOCK = "master_lock";
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = CustomThreadPool.getScheduledExecutor();
    @Value("${netty.server.expire:10}")
    private Integer expire;

    public static boolean isMaster = false;


    public ElectionConfiguration(RedisLockRegistry redisLockRegistry,
                                 WorkflowService workflowService,
                                 RedisHelper redisHelper,
                                 CurrentMasterService currentMasterService,
                                 ExecuteService executeService) {
        this.redisLockRegistry = redisLockRegistry;
        this.workflowService = workflowService;
        this.redisHelper = redisHelper;
        this.currentMasterService = currentMasterService;
        this.executeService = executeService;
    }

    @PostConstruct
    public void election() {
        String currentUrl = currentMasterService.getCurrentIp();
        // Redis???
        Lock lock = redisLockRegistry.obtain(MASTER_ELECTION_LOCK);
        scheduledThreadPoolExecutor
                .scheduleAtFixedRate(() -> {
                    try {
                        // ??????????????????????????????
                        if (redisHelper.hasKey(MASTER_LOCK)) {
                            String masterUrl = redisHelper.strGet(MASTER_LOCK);
                            if (masterUrl.equals(currentUrl)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("???????????????????????????,??????????????????");
                                }
                                redisHelper.setExpire(MASTER_LOCK, expire);
                            } else {
                                log.info("??????????????????URl??????{}", masterUrl);
                                // ????????????????????????????????????
                                MasterMonitorInfo monitorInfo = MasterMonitorInfo.builder()
                                        .ipAddr(currentUrl)
                                        .master(false)
                                        .standby(true)
                                        .executorInfoList(Collections.singletonList(executeService.getExecutorInfo()))
                                        .build();
                                monitorInfo.setMessageType(CommonConstant.STANDBY_INFO);
                                NettyClient.sendMessage(JSON.toJson(monitorInfo));
                            }
                        } else {
                            log.info("???????????????master");
                            // ???????????????
                            if (lock.tryLock(2, TimeUnit.SECONDS)) {
                                // master?????????????????????
                                // ????????????????????????master
                                redisHelper.strSet(MASTER_LOCK, currentUrl, expire, TimeUnit.SECONDS);
                                isMaster = true;
                                // ????????????????????????????????????????????????
                                workflowService.loadUnfinishedWorkflow();
                                // ??????2s ??????????????????????????????standby???????????????
                                TimeUnit.SECONDS.sleep(2L);
                            } else {
                                log.info("??????????????????");
                            }
                        }
                    } catch (Exception e) {
                        log.error("??????????????? ?????????", e);
                    } finally {
                        try {
                            lock.unlock();
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }, 10, 5, TimeUnit.SECONDS);
    }
}
