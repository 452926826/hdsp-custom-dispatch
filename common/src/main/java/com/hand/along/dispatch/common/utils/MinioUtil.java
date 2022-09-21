package com.hand.along.dispatch.common.utils;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MinioUtil {
    private static MinioClient client;

    public static void initMinio(String endpoint, String key, String secret) {
        try {
            client = new MinioClient(endpoint, key, secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Map<String, Object> upload(InputStream is, String fileName, String bucket) {
        Map<String, Object> res = new HashMap<>(16);
        Map<String, Object> data = new HashMap<>(16);
        data.put("bucketName", bucket);
        data.put("fileName", fileName);
        res.put("code", 500);
        try {
            if (!client.bucketExists(bucket)) {
                client.makeBucket(bucket);
            }
            client.putObject(bucket, fileName, is, new PutObjectOptions(is.available(), -1));
            String objectUrl = client.getObjectUrl(bucket, fileName);
            data.put("url", objectUrl);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            res.put("message", e.getMessage());
            return res;
        }
        res.put("code", 200);
        res.put("message", "上传成功");
        res.put("data", data);
        return res;
    }

    public static List<Item> listFile(String bucket) {
        List<Item> itemList = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = client.listObjects(bucket, "");
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    itemList.add(item);
                }
            }
        } catch (Exception e) {
            log.error("获取文件列表失败！", e);
            throw new IllegalStateException("获取文件列表失败！");
        }
        return itemList;
    }


    public static InputStream download(String bucket, Item item) {
        try {
            String objectName = item.objectName();
            return client.getObject(bucket, objectName);
        } catch (Exception e) {
            log.error("下载文件失败！", e);
            throw new IllegalStateException("下载文件失败！");
        }
    }

    public static InputStream download(String fileName, String bucket) {
        try {
            return client.getObject(bucket, fileName);
        } catch (Exception e) {
            log.error("下载文件失败！", e);
            throw new IllegalStateException("下载文件失败！");
        }
    }

    public static InputStream downloadByUrl(String url, String bucket) {
        try {
            String objectName = url.substring(url.lastIndexOf(bucket) + bucket.length() + 1);
            return client.getObject(bucket, objectName);
        } catch (Exception e) {
            log.error("下载文件失败！", e);
            throw new IllegalStateException("下载文件失败！");
        }
    }
}
