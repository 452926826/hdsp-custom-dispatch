package com.hand.along.dispatch.slave.infra.jobs.process;

import com.hand.along.dispatch.common.app.service.FileStoreService;
import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.ExecutionLog;
import com.hand.along.dispatch.common.domain.Job;
import com.hand.along.dispatch.common.domain.JobExecution;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.infra.job.AbstractJob;
import com.hand.along.dispatch.common.infra.mapper.ExecutionLogMapper;
import com.hand.along.dispatch.common.infra.mapper.JobExecutionMapper;
import com.hand.along.dispatch.common.infra.mapper.JobMapper;
import com.hand.along.dispatch.common.utils.ApplicationHelper;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.common.utils.VariableUtil;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 包裹运行时异常
 */
@Slf4j
public abstract class AbstractCommonJob extends AbstractJob {
    public static JobMapper jobMapper;
    public static JobExecutionMapper jobExecutionMapper;
    public static ExecutionLogMapper executionLogMapper;
    public static Job job;
    public static JobExecution jobExecution;
    public static FileStoreService fileStoreService;
    public static DriverSessionService driverSessionService;
    public static Environment environment;

    /**
     * 处理之前
     */
    @Override
    public void before() {
        JobNode jobNode = getJobNode();
        if (Objects.isNull(jobMapper)) {
            jobMapper = ApplicationHelper.getBean(JobMapper.class);
        }
        if (Objects.isNull(jobExecutionMapper)) {
            jobExecutionMapper = ApplicationHelper.getBean(JobExecutionMapper.class);
        }
        if (Objects.isNull(executionLogMapper)) {
            executionLogMapper = ApplicationHelper.getBean(ExecutionLogMapper.class);
        }
        if (Objects.isNull(fileStoreService)) {
            fileStoreService = ApplicationHelper.getBean(FileStoreService.class);
        }
        if (Objects.isNull(driverSessionService)) {
            driverSessionService = ApplicationHelper.getBean(DriverSessionService.class);
        }
        if (Objects.isNull(environment)) {
            environment = ApplicationHelper.getBean(Environment.class);
        }
        job = jobMapper.selectByPrimaryKey(Long.valueOf(jobNode.getObjectId()));
        jobExecution = JobExecution.builder()
                .executionId(jobNode.getExecutionId())
                .startDate(CommonUtil.now())
                .executionParam(JSON.toJson(jobNode.getGlobalParamMap()))
                .graphId(jobNode.getId())
                .jobSettings(job.getJobSettings())
                .jobId(job.getJobId())
                .executionStatus(CommonConstant.ExecutionStatus.READY.name())
                .build();
        jobExecutionMapper.insertSelective(jobExecution);
        ExecutionLog executionLog = ExecutionLog.builder()
                .executionId(jobExecution.getJobExecutionId())
                .executionType(CommonConstant.NodeType.JOB.name())
                .build();
        executionLogMapper.insert(executionLog);
        jobExecution.setExecutionLog(executionLog);
    }

    public String getTmpDataDir(){
        return environment.getProperty("slave.tmp.dir", "data");
    }

    /**
     * 执行
     */
    @Override
    public void run() {
        JobNode jobNode = getJobNode();
        try {
            before();
            jobExecution.setExecutionStatus(CommonConstant.ExecutionStatus.RUNNING.name());
            jobExecutionMapper.updateByPrimaryKey(jobExecution);
            handle();
            after();
        } catch (Exception e) {
            log.error("任务执行失败:{}", jobNode.getId(), e);
            jobExecution.error(e,"任务执行失败");
            jobNode.setStatus(CommonConstant.ExecutionStatus.FAILED.name());
            jobNode.setEndDate(CommonUtil.now());
            NettyClient.sendMessage(JSON.toJson(jobNode));
            jobExecution.setExecutionStatus(CommonConstant.ExecutionStatus.FAILED.name());
            jobExecutionMapper.updateByPrimaryKey(jobExecution);
            updateJobLog();
        }
    }

    public void updateJobLog(){
        List<String> logs = jobExecution.getLogs();
        ExecutionLog executionLog = jobExecution.getExecutionLog();
        executionLog.setExecutionLog(String.format("%s\n%s", StringUtils.isEmpty(executionLog.getExecutionLog()) ? "" : executionLog.getExecutionLog(), StringUtils.join(logs, "\n")));
        executionLogMapper.updateByPrimaryKeyWithBLOBs(executionLog);
    }

    public String replaceJobVariable(String command){
        JobNode jobNode = getJobNode();
        Map<String, Object> localParams = this.getLocalParams();
        Map<String, Object> globalParamMap = jobNode.getGlobalParamMap();
        return VariableUtil.replaceVariable(VariableUtil.replaceVariable(command, globalParamMap), localParams);
    }
}
