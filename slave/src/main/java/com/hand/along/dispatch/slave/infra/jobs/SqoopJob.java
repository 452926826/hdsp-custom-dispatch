package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.PasswordDecoder;
import com.hand.along.dispatch.slave.infra.constants.JobConstant;
import com.hand.along.dispatch.slave.infra.jobs.process.ProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Properties;

@Slf4j
public class SqoopJob extends ProcessJob {
    private static final String JOB_TYPE = "sqoop";

    /**
     * 处理详情
     */
    @Override
    public void handle() {
        String id = getJobNode().getId();
        log.info("执行sqoop任务：{}", id);
        jobExecution.info("执行sqoop任务:{}", id);
        String jobContent = job.getJobContent();
        if (StringUtils.isEmpty(jobContent)) {
            throw new CommonException(id + "--> 任务内容为空！");
        }
        Properties properties = CommonUtil.string2Properties(jobContent);
        String command = properties.get(JobConstant.SQOOP_COMMAND.getKey()).toString();
        String decodeCommand = this.replaceJobVariable(PasswordDecoder.decodePassword(command));
        if(log.isDebugEnabled()){
            log.debug("解密后的命令为：{}",decodeCommand);
        }
        setCommandList(Collections.singletonList(decodeCommand));
    }

    @Override
    public void after() {
        super.after();
        // todo：血缘
    }

    @Override
    public BaseJob getInstance() {
        return new SqoopJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
