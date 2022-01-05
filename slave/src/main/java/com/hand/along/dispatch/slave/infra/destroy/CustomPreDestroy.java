package com.hand.along.dispatch.slave.infra.destroy;

import com.hand.along.dispatch.common.infra.classLoader.PluginUtil;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

@Component
@Slf4j
public class CustomPreDestroy {

    @PostConstruct
    public void init(){
        File dir = new File("jobPlugins");
        ClassLoader classLoader = PluginUtil.loadPlugins(dir, this.getClass().getClassLoader());
        PluginUtil.loadPluginProperties(dir, classLoader);
        log.info("加载任务插件完成");
    }

    @PreDestroy
    public void preDestroy() {
        log.error("slave被停止");
        NettyClient.getChannel().close();
    }
}
