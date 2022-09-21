package com.hand.along.dispatch.common.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * 文件存储配置
 *
 * @author zhilong.deng
 */
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class AlertProperties {

    private String host;

    private String username;

    private String password;

    private Integer port;

    private Properties properties;
}
