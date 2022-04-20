package com.hand.along.dispatch.common.domain;

import com.hand.along.dispatch.common.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecution extends BaseDomain {
    private Long jobExecutionId;

    private Long executionId;

    private String graphId;

    private Long jobId;

    private String executionStatus;

    private Date startDate;

    private Date endDate;

    private String jobSettings;

    private String executionParam;

    @Transient
    private List<String> logs;

    @Transient
    private ExecutionLog executionLog;

    public void info(String log, String... args) {
        if (Objects.isNull(logs)) {
            logs = new ArrayList<>();
        }
        logs.add(String.format("%s INFO %s", CommonUtil.formatNow(), String.format(StringUtils.replace(log, "{}", "%s"), (Object[]) args)));
    }

    public void warn(String log, String... args) {
        if (Objects.isNull(logs)) {
            logs = new ArrayList<>();
        }
        logs.add(String.format("%s WARN %s", CommonUtil.formatNow(), String.format(StringUtils.replace(log, "{}", "%s"), (Object[]) args)));
    }

    public void error(Throwable cause, String log, String... args) {
        if (Objects.isNull(logs)) {
            logs = new ArrayList<>();
        }
        logs.add(String.format("%s ERROR %s", CommonUtil.formatNow(), String.format(StringUtils.replace(log, "{}", "%s"), (Object[]) args)));
        logs.add(String.format("%s ERROR %s", CommonUtil.formatNow(), cause.getMessage()));
        for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
            logs.add(stackTraceElement.toString());
        }
    }
    public void error(String log, String... args) {
        if (Objects.isNull(logs)) {
            logs = new ArrayList<>();
        }
        logs.add(String.format("%s ERROR %s", CommonUtil.formatNow(), String.format(StringUtils.replace(log, "{}", "%s"), (Object[]) args)));
    }
}