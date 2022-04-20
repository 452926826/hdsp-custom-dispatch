package com.hand.along.dispatch.slave.infra.jobs.process;

import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.utils.OSUtils;
import com.hand.along.dispatch.slave.infra.log.LogReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharUtils;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 命令行形式的job抽象类
 */
@Slf4j
public abstract class ProcessJob extends AbstractProcessJob {
    private volatile Process process;
    private List<String> commandList;

    @Override
    public void after() {
        JobNode jobNode = getJobNode();
        try {
            for (String command : commandList) {
                log.info("执行命令：{}", command);
                jobExecution.info("执行命令：{}", command);
                List<String> commands = new ArrayList<>();
                commands.add(OSUtils.isWindows() ? "cmd.exe" : "");
                commands.add(OSUtils.isWindows() ? "/c" : "");
                commands.addAll(Arrays.asList(partitionCommandLine(command)));
                ProcessBuilder processBuilder = new ProcessBuilder(commands);
                ApplicationHome h = new ApplicationHome(getClass());
                String workDir = h.getSource() == null ? h.getDir().toString() : h.getSource().getParentFile().toString();
                processBuilder.directory(new File(workDir));
                processBuilder.environment().putAll(System.getenv());
                processBuilder.redirectErrorStream(true);
                process = processBuilder.start();
                InputStream is = process.getInputStream();
                InputStream es = process.getErrorStream();
                LogReader outputReader = new LogReader(is, log, jobExecution, executionLogMapper, "INFO");
                LogReader errorReader = new LogReader(es, log, jobExecution, executionLogMapper, "ERROR");
                outputReader.start();
                errorReader.start();
                process.waitFor();
                outputReader.awaitCompletion(5000);
                errorReader.awaitCompletion(5000);
                // 最后更新所有日志
                updateJobLog();
                if (process.exitValue() != 0) {
                    log.error("命令 [{}] 执行异常", command);
                    jobExecution.error("命令 [{}] 执行异常", command);
                    updateJobLog();
                    throw new CommonException(String.format("命令 %s 执行异常:%s", command, jobExecution.getExecutionLog().getExecutionLog()));
                }
            }
        } catch (Exception e) {
            log.error("任务执行失败:{}", jobNode.getId(), e);
            jobExecution.error(e, "任务执行失败:{}", jobNode.getId());
            updateJobLog();
            throw new CommonException(e);
        } finally {
            if (Objects.nonNull(process)) {
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getOutputStream());
                IOUtils.closeQuietly(process.getErrorStream());
            }
            // 任务执行成功 进入状态修改
            super.after();
        }
    }

    @Override
    public void cancel() {
        process.destroy();
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }

    public static String[] partitionCommandLine(final String command) {
        ArrayList<String> commands = new ArrayList<String>();

        int index = 0;

        StringBuffer buffer = new StringBuffer(command.length());

        boolean isApos = false;
        boolean isQuote = false;
        while (index < command.length()) {
            char c = command.charAt(index);

            switch (c) {
                case ' ':
                    if (!isQuote && !isApos) {
                        String arg = buffer.toString();
                        buffer = new StringBuffer(command.length() - index);
                        if (arg.length() > 0) {
                            commands.add(arg);
                        }
                    } else {
                        buffer.append(c);
                    }
                    break;
                case '\'':
                    if (!isQuote) {
                        isApos = !isApos;
                    } else {
                        buffer.append(c);
                    }
                    break;
                case '"':
                    if (CharUtils.toString(command.charAt(index - 1)).equals("\\")) {
                        buffer.deleteCharAt(buffer.length() - 1);
                        buffer.append(c);
                    } else {
                        if (!isApos) {
                            isQuote = !isQuote;
                        } else {
                            buffer.append(c);
                        }
                    }
                    break;
                default:
                    buffer.append(c);
            }

            index++;
        }

        if (buffer.length() > 0) {
            String arg = buffer.toString();
            commands.add(arg);
        }
        return commands.toArray(new String[commands.size()]);
    }
}
