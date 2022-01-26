package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.Job;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.common.utils.VariableUtil;
import com.hand.along.dispatch.slave.infra.jobs.process.AbstractProcessJob;
import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SetVariableJob extends AbstractProcessJob {
    private static final String JOB_TYPE = "set_variable";

    /**
     * 处理详情
     */
    @Override
    public void handle() {
        JobNode jobNode = getJobNode();
        try {
            Map<String, Object> globalParamMap = jobNode.getGlobalParamMap();
            Job setVariableJob = jobMapper.selectByPrimaryKey(Long.valueOf(jobNode.getObjectId()));
            String jobSettings = setVariableJob.getJobSettings();
            SetVariableSettings setVariableSettings = JSON.toObj(jobSettings, SetVariableSettings.class);
            String variableValue = setVariableSettings.getVariableValue();
            String replaceVariable = VariableUtil.replaceVariable(variableValue, globalParamMap);
            String evalValue = VariableUtil.evalValue(replaceVariable);
            globalParamMap.put(setVariableSettings.variableCode, evalValue);
            jobNode.setGlobalParamMap(globalParamMap);
            log.info("this is set variable job!");
        } catch (Exception e) {
            log.info("任务执行失败:{}", jobNode.getId());
            jobNode.setStatus(CommonConstant.ExecutionStatus.FAILED.name());
            jobNode.setEndDate(CommonUtil.now());
            NettyClient.sendMessage(JSON.toJson(jobNode));
        }
    }

    @Override
    public BaseJob getInstance() {
        return new SetVariableJob();
    }

    @Override
    public String getJobType() {
        return JOB_TYPE;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SetVariableSettings {
        private String variableCode;
        private String variableValue;
    }

}
