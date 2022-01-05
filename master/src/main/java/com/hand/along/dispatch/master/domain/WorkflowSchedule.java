package com.hand.along.dispatch.master.domain;

import com.hand.along.dispatch.common.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowSchedule extends BaseDomain {
    private Long scheduleId;

    private Long workflowId;

    private String workflowName;

    private String workflowCode;

    private String cronExpression;

    @Transient
    private String graph;

}