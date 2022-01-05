package com.hand.along.dispatch.master.infra.netty;

import com.hand.along.dispatch.common.exceptions.CommonException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;

@Slf4j
public class NettyServer {
    //用于保存所有Channel对象
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Channel channel;

    /**
     * 启动netty服务
     *
     * @param socketAddress 地址
     */
    public void start(InetSocketAddress socketAddress) {
        //new 一个主线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //new 一个工作线程组
        EventLoopGroup workGroup = new NioEventLoopGroup(200);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer())
                .localAddress(socketAddress)
                //设置队列大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        //绑定端口,开始接收进来的连接
        try {
            ChannelFuture future = bootstrap.bind(socketAddress).sync();
            log.info("服务器启动开始监听端口: {}", socketAddress.getPort());
            Channel channel = future.channel();
            NettyServer.channel = channel;
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务启动异常", e);
            e.printStackTrace();
        } finally {
            //关闭主线程组
            bossGroup.shutdownGracefully();
            //关闭工作线程组
            workGroup.shutdownGracefully();
            NettyServer.channel.close();
        }
    }

    public static Channel getChannel() {
        return channel;
    }

    /**
     * 发送给第一个slave
     *
     * @param message 消息
     */
    public static void sendFirst(String message) {
        if (CollectionUtils.isNotEmpty(channelGroup)) {
            Channel channel = channelGroup.stream().findFirst().get();
            channel.writeAndFlush(message);
        } else {
            throw new CommonException("当前没有slave接入！");
        }
    }

    /**
     * 随机发送给slave
     *
     * @param message 消息
     */
    public static void sendRandom(String message) {
        if (CollectionUtils.isNotEmpty(channelGroup)) {
            int i = new Random().nextInt(channelGroup.size());
            Channel channel = new ArrayList<>(channelGroup).get(i);
            channel.writeAndFlush(message);
        } else {
            throw new CommonException("当前没有slave接入！");
        }
    }

    /**
     * 给所有的salve发消息
     *
     * @param message 消息
     */
    public static void sendAll(String message) {
        channelGroup.writeAndFlush(message);
    }
}
