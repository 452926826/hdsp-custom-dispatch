package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.PasswordDecoder;
import com.hand.along.dispatch.slave.infra.jobs.process.ProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

@Slf4j
public class CommandJob extends ProcessJob {
    private static final String JOB_TYPE = "command";

    /**
     * 处理详情
     */
    @Override
    public void handle() {
        String id = getJobNode().getId();
        log.info("执行command任务：{}", id);
        jobExecution.info("执行command任务:{}", id);
        String jobContent = job.getJobContent();
        if (StringUtils.isEmpty(jobContent)) {
            throw new CommonException(id + "--> 任务内容为空！");
        }
        String decodeCommand = this.replaceJobVariable(PasswordDecoder.decodePassword(jobContent));
        if (log.isDebugEnabled()) {
            log.debug("解密后的命令为：{}", decodeCommand);
        }
        setCommandList(Collections.singletonList(decodeCommand));
    }

    @Override
    public BaseJob getInstance() {
        return new CommandJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

}
