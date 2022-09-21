package com.hand.along.dispatch.common.app.service.impl;

import com.hand.along.dispatch.common.app.service.FileStoreService;
import com.hand.along.dispatch.common.infra.config.FileStoreProperties;
import com.hand.along.dispatch.common.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@ConditionalOnProperty(prefix = "file.store", name = "type", havingValue = "minio")
@Service
@Slf4j
public class MinioStoreServiceImpl implements FileStoreService {
    private final FileStoreProperties fileStoreProperties;

    public MinioStoreServiceImpl(FileStoreProperties fileStoreProperties) {
        this.fileStoreProperties = fileStoreProperties;
        MinioUtil.initMinio(fileStoreProperties.getUrl(), fileStoreProperties.getAccess(), fileStoreProperties.getSecret());
    }

    @Override
    public String uploadFile(String fileName, InputStream is) {
        Map<String, Object> upload = MinioUtil.upload(is, fileName, fileStoreProperties.getDir());
        return ((Map) upload.get("data")).get("url").toString();
    }

    @Override
    public InputStream downloadFile(String filePath) {
        return MinioUtil.downloadByUrl(filePath, fileStoreProperties.getDir());
    }
}
