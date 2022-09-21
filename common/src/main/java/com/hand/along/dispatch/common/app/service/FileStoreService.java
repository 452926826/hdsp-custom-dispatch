package com.hand.along.dispatch.common.app.service;

import java.io.InputStream;

public interface FileStoreService {
    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param is       文件流
     */
    String uploadFile(String fileName, InputStream is);

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件流
     */
    InputStream downloadFile(String filePath);
}
