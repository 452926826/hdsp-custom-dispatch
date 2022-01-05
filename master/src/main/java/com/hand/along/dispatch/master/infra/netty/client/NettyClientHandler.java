package com.hand.along.dispatch.master.infra.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


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
        log.info("收到master发来的消息: {}", message);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("master下线");
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
