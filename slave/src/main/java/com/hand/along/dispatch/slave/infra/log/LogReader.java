package com.hand.along.dispatch.slave.infra.log;

import com.hand.along.dispatch.common.domain.ExecutionLog;
import com.hand.along.dispatch.common.domain.JobExecution;
import com.hand.along.dispatch.common.infra.mapper.ExecutionLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LogReader extends Thread {
    private final InputStream is;
    private final Logger log;
    private final JobExecution jobExecution;
    private final String logType;
    private final ExecutionLogMapper executionLogMapper;

    public LogReader(InputStream is, Logger log, JobExecution jobExecution, ExecutionLogMapper executionLogMapper, String logType) {
        this.is = is;
        this.log = log;
        this.jobExecution = jobExecution;
        this.executionLogMapper = executionLogMapper;
        this.logType = logType;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(is));
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String line = inputReader.readLine();
                if (line == null) {
                    return;
                }
                if ("ERROR".equals(logType)) {
                    jobExecution.error(line);
                } else if ("INFO".equals(logType)) {
                    jobExecution.info(line);
                } else {
                    jobExecution.warn(line);
                }
                // 每10行日志 更新一次数据库
                List<String> logs = jobExecution.getLogs();
                if (logs.size() > 10) {
                    ExecutionLog executionLog = jobExecution.getExecutionLog();
                    executionLog.setExecutionLog(String.format("%s\n%s", StringUtils.isEmpty(executionLog.getExecutionLog()) ? "" : executionLog.getExecutionLog(), StringUtils.join(logs, "\n")));
                    executionLogMapper.updateByPrimaryKeyWithBLOBs(executionLog);
                    logs.clear();
                }

            }
        } catch (IOException e) {
            log.error("读取日志流失败", e);
        }
    }


    public void awaitCompletion(final long waitMs) {
        try {
            join(waitMs);
        } catch (InterruptedException e) {
            log.error("I/O thread interrupted.", e);
        }
    }
}
