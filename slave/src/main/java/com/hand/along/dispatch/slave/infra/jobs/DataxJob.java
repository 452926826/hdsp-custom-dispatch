package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.slave.infra.jobs.process.ProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Properties;

@Slf4j
public class DataxJob extends ProcessJob {
    private static final String JOB_TYPE = "datax";

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
        String command = String.format("python %s/bin/datax.py %s %s",
                properties.getProperty("datax.home"),
                properties.getProperty("datax.jvmParam", ""),
                properties.getProperty("datax.scripts"));
        // todo 需要替换脚本里面的参数
        setCommandList(Collections.singletonList(command));
    }

    @Override
    public BaseJob getInstance() {
        return new DataxJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
