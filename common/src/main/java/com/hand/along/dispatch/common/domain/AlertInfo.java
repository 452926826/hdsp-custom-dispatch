package com.hand.along.dispatch.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 告警信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertInfo {
    private String message;
    private String mailList;
    private String subject;
    /**
     * 告警对象 JOB/WORKFLOW
     */
    private String alertObject;
    /**
     * 告警类型 FAIl/SUCCESS
     */
    private String alertType;

    private String workflowName;

    private String jobName;

    private Date alertDate;

    private String log;
}
