package com.hand.along.dispatch.slave.infra.netty;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.BaseMessage;
import com.hand.along.dispatch.common.domain.ExecutorInfo;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.slave.infra.handler.JobHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * netty  客户端处理
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 客户端连接到服务端触发
     *
     * @param ctx context
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端Active .....");
    }

    /**
     * 客户端收到消息触发
     *
     * @param ctx context
     * @param msg 消息
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = msg.toString();
        log.info("客户端收到消息: {}", message);
        BaseMessage tmp = JSON.toObj(message, BaseMessage.class);
        if (CommonConstant.JOB.equals(tmp.getMessageType())) {
            NettyClient.putMessage(message);
        } else if (CommonConstant.INFO.equals(tmp.getMessageType())) {
            ThreadPoolExecutor threadPoolExecutor = JobHandler.threadPoolExecutor;
            ExecutorInfo executorInfo = ExecutorInfo.builder()
                    .activeCount(threadPoolExecutor.getActiveCount())
                    .completedTaskCount(threadPoolExecutor.getCompletedTaskCount())
                    .taskCount(threadPoolExecutor.getTaskCount())
                    .corePoolSize(threadPoolExecutor.getCorePoolSize())
                    .maximumPoolSize(threadPoolExecutor.getMaximumPoolSize())
                    .queueCount(threadPoolExecutor.getQueue().size())
                    .build();
            executorInfo.setMessageType(CommonConstant.INFO);
            NettyClient.sendMessage(JSON.toJson(executorInfo));
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端下线");
    }

    /**
     * 异常触发
     *
     * @param ctx   context
     * @param cause 异常
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
