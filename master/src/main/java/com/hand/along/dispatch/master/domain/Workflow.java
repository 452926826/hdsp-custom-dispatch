package com.hand.along.dispatch.master.domain;

import com.google.common.graph.Graph;
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
public class Workflow extends BaseDomain {

    private Long workflowId;

    /**
     * 任务流名称
     */
    private String workflowName;

    /**
     * 任务流编码
     */
    private String workflowCode;

    /**
     * 任务流描述
     */
    private String workflowDescription;

    /**
     * 任务流图形
     */
    private String graph;

    /**
     * 任务流优先级（PRESSING，HIGHER，NORMAL，LOWER,UNIMPORTANT）
     */
    private String priorityLevel;


    @Transient
    private Graph graphObj;

    @Transient
    private String messageType;
}
