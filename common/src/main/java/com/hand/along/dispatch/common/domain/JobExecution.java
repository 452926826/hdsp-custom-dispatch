package com.hand.along.dispatch.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

}