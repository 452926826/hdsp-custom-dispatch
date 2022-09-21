package com.hand.along.dispatch.slave.infra.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        // 读取前置sql任务的数据
        File dir = new File(String.format("%s/%s/", getTmpDataDir(), jobExecution.getExecutionId()));
        FileUtils.listFiles(dir, new String[]{"json"}, true).forEach(f -> {
            String fileId = f.getName().replace(".json", "");
            List<List<Map<String, Object>>> results;
            try {
                results = JSON.fromJson(FileUtils.readFileToString(f, StandardCharsets.UTF_8), new TypeReference<List<List<Map<String, Object>>>>() {
                });
                List<List<Map<String, Object>>> tmp = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(results)) {
                    for (List<Map<String, Object>> result : results) {
                        if (CollectionUtils.isNotEmpty(result)) {
                            Map<String, Object> tmpMap = result.get(0);
                            tmpMap.forEach((k, v) -> globalParamMap.put(String.format("%s.%s", fileId, k), v));
                            result.remove(0);
                            if (CollectionUtils.isNotEmpty(result)) {
                                tmp.add(result);
                            }
                        }
                    }
                }
                FileUtils.writeStringToFile(f, JSON.toJson(tmp), StandardCharsets.UTF_8, false);
            } catch (IOException e) {
                log.error("sql结果读取失败",e);
            }
        });
        String jobSettings = job.getJobSettings();
        List<SetVariableSettings> setVariableSettings = JSON.fromJson(jobSettings, new TypeReference<List<SetVariableSettings>>() {
        });
        for (SetVariableSettings setVariableSetting : setVariableSettings) {
            String variableValue = setVariableSetting.getVariableValue();
            String replaceVariable = VariableUtil.replaceVariable(variableValue, globalParamMap);
            String evalValue = VariableUtil.evalValue(replaceVariable);
            globalParamMap.put(setVariableSetting.variableCode, evalValue);
        }
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
