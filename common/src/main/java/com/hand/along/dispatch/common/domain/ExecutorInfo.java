package com.hand.along.dispatch.common.domain;

import com.hand.along.dispatch.common.domain.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorInfo extends BaseMessage {
    private Integer activeCount;
    private Long taskCount;
    private Long completedTaskCount;
    private Integer queueCount;

    private Integer maximumPoolSize;
    private Integer corePoolSize;
}
