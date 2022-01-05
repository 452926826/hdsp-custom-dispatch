package com.hand.along.dispatch.common.domain;

import com.hand.along.dispatch.common.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseDomain {
    private Long jobId;

    private String jobName;

    private String jobCode;

    private String jobDescription;

    private String jobType;

    private String priorityLevel;

    private String jobSettings;

}