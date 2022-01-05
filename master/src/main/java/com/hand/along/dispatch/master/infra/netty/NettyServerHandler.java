package com.hand.along.dispatch.master.infra.netty;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.BaseMessage;
import com.hand.along.dispatch.common.domain.ExecutorInfo;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.utils.ApplicationHelper;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.master.app.service.WorkflowService;
import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.master.infra.election.CurrentMasterService;
import com.hand.along.dispatch.master.infra.handler.WorkflowJob;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hand.along.dispatch.master.infra.netty.NettyServer.channelGroup;

/**
 * netty服务端处理器
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    public static final Map<String, ExecutorInfo> slaveExecutorInfoMap = new HashMap<>();
    private final WorkflowService workflowService = ApplicationHelper.getBean(WorkflowService.class);

    private final CurrentMasterService currentMasterService = ApplicationHelper.getBean(CurrentMasterService.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.add(channel);
    }

    /**
     * 客户端连接会触发
     *
     * @param ctx context
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active......");
        Channel channel = ctx.channel();
        log.warn(channel.remoteAddress() + "上线");

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.warn(channel.remoteAddress() + "离线");
    }

    /**
     * 客户端发消息会触发
     *
     * @param ctx context
     * @param msg 消息
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        ConcurrentHashMap<String, JobNode> activeNodeMap = WorkflowJob.activeNodeMap;
        String message = msg.toString();
        log.info("服务器收到消息: {}", message);
        BaseMessage tmp = JSON.toObj(message, BaseMessage.class);
        if (CommonConstant.JOB.equals(tmp.getMessageType())) {
            JobNode jobNode = JSON.fromJson(message, JobNode.class);
            String uniqueId = jobNode.getUniqueId();
            if (activeNodeMap.containsKey(uniqueId)) {
                activeNodeMap.get(uniqueId).setStatus(jobNode.getStatus());
            }
        } else if (CommonConstant.INFO.equals(tmp.getMessageType())) {
            slaveExecutorInfoMap.put(channel.remoteAddress().toString(), JSON.fromJson(message, ExecutorInfo.class));
        } else if (CommonConstant.EXECUTE_WORKFLOW.equals(tmp.getMessageType())) {
            Workflow workflow = JSON.fromJson(message, Workflow.class);
            workflowService.execute(workflow);
        } else if (CommonConstant.CRON_WORKFLOW.equals(tmp.getMessageType())) {
            Workflow workflow = JSON.fromJson(message, Workflow.class);
            workflowService.cron(workflow);
        } else if (CommonConstant.STANDBY_INFO.equals(tmp.getMessageType())){
            currentMasterService.setStandby(message);
        }

    }

    /**
     * 发生异常会触发
     *
     * @param ctx   context
     * @param cause 异常
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("异常", cause);
        ctx.close();
    }
}
