package com.hand.along.dispatch.slave.infra.handler;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.infra.classLoader.PluginUtil;
import com.hand.along.dispatch.common.infra.job.AbstractJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.CustomThreadPool;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务处理类
 */
@Component
@Slf4j
public class JobHandler {
    public static final ThreadPoolExecutor threadPoolExecutor = CustomThreadPool.getExecutor();
    private static final Map<String,JobNode> activeSubWorkflow = new ConcurrentHashMap<>();

    /**
     * 提交任务
     *
     * @param message 任务信息
     */
    public void submitJob(String message) {
        log.info("获取到一个任务：{}", message);
        JobNode jobNode = JSON.toObj(message, JobNode.class);
        try {
            jobNode.setStartDate(CommonUtil.now());
            handler(jobNode);
        } catch (Exception e) {
            log.error("任务执行失败", e);
            jobNode.setStatus(CommonConstant.ExecutionStatus.FAILED.name());
            jobNode.setEndDate(CommonUtil.now());
            NettyClient.sendMessage(JSON.toJson(jobNode));
        }
    }

    /**
     * 处理
     *
     * @param jobNode node
     */
    private void handler(JobNode jobNode) {
        log.info("处理：{}", jobNode.getId());
        Map<String, Object> params = new HashMap<>();
        params.put("id", jobNode.getUniqueId());
        AbstractJob abstractJob = PluginUtil.newInstance(jobNode, params);
        threadPoolExecutor.execute(abstractJob);
    }

    public static void putSubWorkflow(JobNode jobNode){
        activeSubWorkflow.put(jobNode.getUuid(), jobNode);
    }

    public static JobNode getSubWorkflow(String uuid){
        return activeSubWorkflow.getOrDefault(uuid, null);
    }
}
