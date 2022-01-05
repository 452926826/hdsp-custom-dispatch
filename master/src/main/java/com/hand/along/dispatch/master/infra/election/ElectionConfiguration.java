package com.hand.along.dispatch.master.infra.election;

import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.CustomThreadPool;
import com.hand.along.dispatch.common.utils.RedisHelper;
import com.hand.along.dispatch.master.app.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.hand.along.dispatch.common.constants.CommonConstant.MASTER_LOCK;
import static com.hand.along.dispatch.common.constants.CommonConstant.STANDBY_MASTER;

@Component
@Slf4j
public class ElectionConfiguration {
    private final RedisLockRegistry redisLockRegistry;
    private final WorkflowService workflowService;
    private final RedisHelper redisHelper;
    private static final String MASTER_ELECTION_LOCK = "master_lock";
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = CustomThreadPool.getScheduledExecutor();
    @Value("${netty.server.serverPort:31020}")
    private Integer serverPort;
    @Value("${netty.server.ip-pattern}")
    private String pattern;
    @Value("${netty.server.expire:10}")
    private Integer expire;

    public static boolean isMaster = false;


    public ElectionConfiguration(RedisLockRegistry redisLockRegistry,
                                 WorkflowService workflowService,
                                 RedisHelper redisHelper) {
        this.redisLockRegistry = redisLockRegistry;
        this.workflowService = workflowService;
        this.redisHelper = redisHelper;
    }

    @PostConstruct
    public void election() {
        String ip = CommonUtil.getIp(pattern);
        String currentUrl = String.format("%s:%s", ip, serverPort);
        // Redis锁
        Lock lock = redisLockRegistry.obtain(MASTER_ELECTION_LOCK);
        scheduledThreadPoolExecutor
                .scheduleAtFixedRate(() -> {
                    try {
                        // master已经选举出来了
                        if (redisHelper.hasKey(MASTER_LOCK)) {
                            String masterUrl = redisHelper.strGet(MASTER_LOCK);
                            if (masterUrl.equals(currentUrl)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("当前服务就是master,重置过期时间");
                                }
                                redisHelper.setExpire(MASTER_LOCK, expire);
                            } else {
                                log.info("当前master的URl为：{}", masterUrl);
                                // 放到standby
                                redisHelper.setIrt(STANDBY_MASTER, currentUrl);
                            }
                        } else {
                            log.info("尝试选举为master");
                            // 尝试获取锁
                            if (lock.tryLock(2, TimeUnit.SECONDS)) {
                                // master并没有选举出来
                                // 把当前服务设置为master
                                redisHelper.strSet(MASTER_LOCK, currentUrl, expire, TimeUnit.SECONDS);
                                isMaster = true;
                                // 删除standby的数据
                                redisHelper.setDel(STANDBY_MASTER, currentUrl);
                                // 此时需要加载所有没有运行完的程序
                                workflowService.loadUnfinishedWorkflow();
                                // 等待2s 让没有获取到锁的其他standby的节点异常
                                TimeUnit.SECONDS.sleep(2L);
                            } else {
                                log.info("没获取到锁！");
                            }
                        }
                    } catch (Exception e) {
                        log.error("获取锁失败 ，忽略", e);
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
