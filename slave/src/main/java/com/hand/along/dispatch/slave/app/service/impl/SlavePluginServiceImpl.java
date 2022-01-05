package com.hand.along.dispatch.slave.app.service.impl;

import com.hand.along.dispatch.common.infra.classLoader.PluginUtil;
import com.hand.along.dispatch.slave.app.service.SlavePluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class SlavePluginServiceImpl implements SlavePluginService {

    @Override
    public void reload() {
        File dir = new File("jobPlugins");
        ClassLoader classLoader = PluginUtil.loadPlugins(dir, this.getClass().getClassLoader());
        PluginUtil.loadPluginProperties(dir, classLoader);
        log.info("加载任务插件完成");
    }

}
