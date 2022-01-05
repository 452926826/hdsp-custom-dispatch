package com.hand.along.dispatch.master.infra.destroy;

import com.hand.along.dispatch.master.infra.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class CustomPreDestroy {

    @PreDestroy
    public void preDestroy() {
        log.error("master被停止，请重启");
        NettyServer.getChannel().close();
    }
}
