package com.hand.along.dispatch.master.infra.configuration;

import com.hand.along.dispatch.master.infra.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
@ComponentScan("com.hand.along")
@Slf4j
public class MasterConfiguration {
    private final static String LOCK_PREFIX = "REDIS_LOCK";

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, LOCK_PREFIX);
    }

}
