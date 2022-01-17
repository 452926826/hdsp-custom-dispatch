package com.hand.along.dispatch.common.infra.classLoader;

import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.AbstractJob;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@Slf4j
public class PluginUtil {
    public final static Map<String, Class<? extends BaseJob>> jobTypes = new HashMap<>();

    static {
        Reflections reflections = new Reflections("com.hand.along.dispatch.slave.infra.jobs");
        //获取实现了BaseJob的所有类
        Set<Class<? extends BaseJob>> classSet = reflections.getSubTypesOf(BaseJob.class);
        classSet.forEach(c -> {
            // 排除抽象类
            if(!Modifier.isAbstract(c.getModifiers())){
                try {
                    BaseJob baseJob = c.newInstance();
                    jobTypes.put(baseJob.getJobType(), c);
                } catch (Exception e) {
                    log.error("获取{}实例失败", c.getName(), e);
                    throw new CommonException(e);
                }
            }

        });
    }


    public static ClassLoader loadPlugins(File dir, ClassLoader parentClassLoader) {
        List<URL> resources = new ArrayList<>();
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"jar"}, true);
        files.forEach(f -> {
            try {
                URL url = f.toURI().toURL();
                resources.add(url);
            } catch (MalformedURLException e) {
                log.error("文件错误：{}", f.getAbsolutePath(), e);
            }
        });
        return new URLClassLoader(resources.toArray(new URL[resources.size()]),
                parentClassLoader);
    }

    public static void loadPluginProperties(File dir, ClassLoader classLoader) {
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"properties"}, true);
        for (File file : files) {
            try {
                InputStream input = new BufferedInputStream(new FileInputStream(file));
                Properties properties = new Properties();
                properties.load(input);
                if (properties.containsKey("job.type") && properties.containsKey("job.class")) {
                    jobTypes.put(properties.getProperty("job.type"), (Class<? extends BaseJob>) classLoader.loadClass(properties.getProperty("job.class")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static AbstractJob newInstance(JobNode jobNode, Map<String, Object> params) {
        String jobType = jobNode.getJobType();
        if (jobTypes.containsKey(jobType)) {
            Class<? extends BaseJob> aClass = jobTypes.get(jobType);
            try {
                AbstractJob abstractJob = (AbstractJob) aClass.newInstance();
                abstractJob.setParams(params);
                abstractJob.setJobNode(jobNode);
                return abstractJob;
            } catch (Exception e) {
                log.error("初始化job异常", e);
                throw new CommonException("初始化job异常");
            }
        } else {
            throw new CommonException("没有找到对应类型的job：" + jobType);
        }
    }
}
