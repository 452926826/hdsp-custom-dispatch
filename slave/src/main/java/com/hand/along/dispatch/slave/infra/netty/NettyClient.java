package com.hand.along.dispatch.slave.infra.netty;

import com.hand.along.dispatch.common.utils.ApplicationHelper;
import com.hand.along.dispatch.slave.infra.handler.JobHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.hand.along.dispatch.common.constants.CommonConstant.LINE_SEPARATOR;

@Slf4j
public class NettyClient {
    /**
     * 缓存还没发出去的信息
     */
    public static final List<String> cachedMessage = new ArrayList<>();
    private static Channel channel;

    /**
     * 启动一个client
     *
     * @param ip   ip地址
     * @param port port端口
     */
    public void start(String ip, Integer port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
        try {
            ChannelFuture future = bootstrap.connect(ip, port).sync();
            log.info("连接server成功....");
            Channel channel = future.channel();
            NettyClient.channel = channel;
            //连接成功后处理缓存的消息
            handlerCachedMessage();
            // 等待连接被关闭
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            channel.close();
            log.error("连接被关闭", e);
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            channel.close();
            log.warn("连接被关闭");
        }
    }

    /**
     * 处理缓存的消息
     */
    private void handlerCachedMessage() {
        cachedMessage.forEach(m-> channel.writeAndFlush(m));
    }

    public static Channel getChannel() {
        return channel;
    }

    /**
     * 消息放入队列中
     * @param message 消息
     */
    public static void putMessage(String message) throws InterruptedException {
        // 消息放入队列中  put 当队列满的时候 阻塞 直至有空间
        JobHandler handler = ApplicationHelper.getApplicationContext().getBean(JobHandler.class);
        handler.submitJob(message);
    }

    /**
     * 发消息
     *
     * @param message 消息
     */
    public static void sendMessage(String message) {
        if (channel.isActive()) {
            // 当前连接是可用的
            log.warn("当前连接可用");
            channel.writeAndFlush(message+LINE_SEPARATOR);
        } else {
            //如果此时master下线了怎么办
            log.warn("当前连接不可用，放入缓存队列中");
            cachedMessage.add(message);
        }
    }
}
