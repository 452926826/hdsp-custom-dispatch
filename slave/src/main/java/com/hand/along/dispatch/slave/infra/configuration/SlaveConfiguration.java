package com.hand.along.dispatch.slave.infra.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@Slf4j
@ComponentScan("com.hand.along")
public class SlaveConfiguration {
    @Value("${slave.tmp.dir:data}")
    private String dirPath;

    @PostConstruct
    public void initSlave() {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
