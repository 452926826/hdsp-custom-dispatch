package com.hand.along.dispatch.master;

import com.hand.along.dispatch.master.infra.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetSocketAddress;

@SpringBootApplication
@MapperScan({
        "com.hand.**.mapper"
})
public class MasterApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext run = SpringApplication.run(MasterApplication.class, args);
            String serverPort = run.getEnvironment().getProperty("netty.server.port");
            //启动服务端
            NettyServer nettyServer = new NettyServer();
            nettyServer.start(new InetSocketAddress("0.0.0.0", Integer.parseInt(serverPort)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
