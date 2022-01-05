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
public class ExecutionLog extends BaseDomain {
    private Long logId;

    private Long executionId;

    private String executionType;

    private String executionLog;
}