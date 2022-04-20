package com.hand.along.dispatch.slave.infra.jobs;

import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.common.utils.VariableUtil;
import com.hand.along.dispatch.slave.infra.jobs.process.AbstractProcessJob;
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
        log.info("this is set variable job!");
        JobNode jobNode = getJobNode();
        Map<String, Object> globalParamMap = jobNode.getGlobalParamMap();
        String jobSettings = job.getJobSettings();
        SetVariableSettings setVariableSettings = JSON.toObj(jobSettings, SetVariableSettings.class);
        String variableValue = setVariableSettings.getVariableValue();
        String replaceVariable = VariableUtil.replaceVariable(variableValue, globalParamMap);
        String evalValue = VariableUtil.evalValue(replaceVariable);
        globalParamMap.put(setVariableSettings.variableCode, evalValue);
        jobNode.setGlobalParamMap(globalParamMap);
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
