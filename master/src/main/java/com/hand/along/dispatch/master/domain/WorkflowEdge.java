package com.hand.along.dispatch.master.domain;

import lombok.*;

/**
 * 描述： workflow节点连线
 *
 * @author zhilong.deng@hand-china.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEdge {
    private String source;
    private Integer sourceAnchor;
    private String target;
    private Integer targetAnchor;
    private String id;
    private Integer index;
    private String condition;
}
