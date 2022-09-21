package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.common.utils.PasswordDecoder;
import com.hand.along.dispatch.slave.infra.constants.JobConstant;
import com.hand.along.dispatch.slave.infra.jobs.process.ProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.driver.core.session.DriverSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class SqlJob extends ProcessJob {
    private static final String JOB_TYPE = "sql";
    private String decodeSql;

    /**
     * 处理详情
     */
    @Override
    public void handle() {
        String id = getJobNode().getId();
        log.info("执行sql任务：{}", id);
        jobExecution.info("执行sql任务:{}", id);
        String jobContent = job.getJobContent();
        if (StringUtils.isEmpty(jobContent)) {
            throw new CommonException(id + "--> 任务内容为空！");
        }
        Properties properties = CommonUtil.string2Properties(jobContent);
        Long tenantId = Long.valueOf(properties.getProperty(JobConstant.SQL_TENANT_ID.getKey(), "0L"));
        String datasourceCode = properties.getProperty(JobConstant.SQL_DATASOURCE_CODE.getKey());
        String schema = properties.getProperty(JobConstant.SQL_SCHEMA.getKey());
        // 拉取文件
        String filePath = properties.getProperty(JobConstant.SQL_SCRIPTS.getKey());
        InputStream inputStream = fileStoreService.downloadFile(filePath);
        try {
            String sql = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            // 解密密码
            decodeSql = this.replaceJobVariable(PasswordDecoder.decodePassword(sql));
            if (log.isDebugEnabled()) {
                log.debug("解密后的sql为：{}", decodeSql);
            }
            DriverSession driverSession = driverSessionService.getDriverSession(tenantId, datasourceCode);
            List<List<Map<String, Object>>> executeAll = driverSession.executeAll(schema, decodeSql, true, false, true);
            if (CollectionUtils.isNotEmpty(executeAll)) {
                File tmp = new File(String.format("%s/%s/%s.json", getTmpDataDir(), jobExecution.getExecutionId(), id));
                FileUtils.writeStringToFile(tmp, JSON.toJson(executeAll), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("写入sql失败", e);
            throw new CommonException("write sql failed", e);
        }
    }

    @Override
    public void after() {
        super.after();
        // todo：血缘
        log.info("sql:{}", decodeSql);
    }

    @Override
    public BaseJob getInstance() {
        return new SqlJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
