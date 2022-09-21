package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.PasswordDecoder;
import com.hand.along.dispatch.slave.infra.constants.JobConstant;
import com.hand.along.dispatch.slave.infra.jobs.process.ProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class DataxJob extends ProcessJob {
    private static final String JOB_TYPE = "datax";
    private File tmpScript;

    @Override
    public void before() {
        super.before();
        try {
            tmpScript = File.createTempFile("hdsp", ".json");
        } catch (IOException e) {
            log.error("创建文件失败", e);
            throw new CommonException("create file failed", e);
        }
    }

    /**
     * 处理详情
     */
    @Override
    public void handle() {
        String id = getJobNode().getId();
        log.info("执行datax任务：{}", id);
        jobExecution.info("执行datax任务:{}", id);
        String jobContent = job.getJobContent();
        if (StringUtils.isEmpty(jobContent)) {
            throw new CommonException(id + "--> 任务内容为空！");
        }
        Properties properties = CommonUtil.string2Properties(jobContent);
        // 拉取文件
        String filePath = properties.getProperty(JobConstant.DATAX_SCRIPTS.getKey());
        InputStream inputStream = fileStoreService.downloadFile(filePath);
        try {
            List<String> commands = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            // 解密密码
            List<String> decodeJson = commands.stream()
                    .map(PasswordDecoder::decodePassword)
                    .map(this::replaceJobVariable)
                    .collect(Collectors.toList());
            if (log.isDebugEnabled()) {
                log.debug("解密后的json为：{}", decodeJson);
            }
            // 写入文件
            FileUtils.writeLines(tmpScript, decodeJson);
        } catch (IOException e) {
            log.error("写入datax脚本失败", e);
            throw new CommonException("write datax failed", e);
        }
        String command = String.format("python %s/bin/datax.py %s %s",
                properties.getProperty(JobConstant.DATAX_HOME.getKey()),
                properties.getProperty(JobConstant.DATAX_JVM_PARAM.getKey(), ""),
                tmpScript.getAbsolutePath());
        setCommandList(Collections.singletonList(command));
    }

    @Override
    public BaseJob getInstance() {
        return new DataxJob();
    }

    @Override
    public void after() {
        super.after();
        FileUtils.deleteQuietly(tmpScript);
        // todo 血缘
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
