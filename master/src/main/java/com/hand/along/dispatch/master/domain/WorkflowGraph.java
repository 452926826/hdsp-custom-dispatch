package com.hand.along.dispatch.master.domain;

import lombok.*;

import java.util.List;

/**
 * 描述：workflow graph图
 *
 * @author zhilong.deng@hand-china.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowGraph {
    private List<WorkflowNode> nodes;
    private List<WorkflowEdge> edges;
}
