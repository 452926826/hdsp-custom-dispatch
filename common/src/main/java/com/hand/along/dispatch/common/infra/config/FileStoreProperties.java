package com.hand.along.dispatch.common.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置
 *
 * @author zhilong.deng
 */
@Data
@ConfigurationProperties(prefix = "file.store")
public class FileStoreProperties {
    /**
     * 文件存储类型  local/minio/hdfs
     */
    private String type;
    /**
     * 文件存储目录 如果是minio则代表桶
     */
    private String dir;

    /**
     * 如果是minio 文件存储url
     */
    private String url;

    /**
     * 如果是minio 文件存储access-key
     */
    private String access;

    /**
     * 如果是minio 文件存储secret-key
     */
    private String secret;
}
