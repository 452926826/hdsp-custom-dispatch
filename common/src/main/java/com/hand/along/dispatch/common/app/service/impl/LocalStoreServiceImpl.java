package com.hand.along.dispatch.common.app.service.impl;

import com.hand.along.dispatch.common.app.service.FileStoreService;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.config.FileStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;

@ConditionalOnProperty(prefix = "file.store",name = "type",havingValue = "local")
@Service
@Slf4j
public class LocalStoreServiceImpl implements FileStoreService {
    @Autowired
    private FileStoreProperties storeProperties;
    @Override
    public String uploadFile(String fileName, InputStream is) {
        try {
            String dir = storeProperties.getDir();
            String filePath = String.format("%s/%s", dir, fileName);
            File tmp = new File(filePath);
            FileUtils.copyInputStreamToFile(is, tmp);
            return tmp.getAbsolutePath();
        } catch (IOException e) {
            log.error("上传文件错误，", e);
            throw new CommonException("上传文件错误", e);
        }
    }

    public InputStream downloadFile(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("下载文件错误，", e);
            throw new CommonException("下载文件错误", e);
        }
    }
}
