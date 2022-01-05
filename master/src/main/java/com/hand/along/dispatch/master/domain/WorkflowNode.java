package com.hand.along.dispatch.master.domain;

import lombok.*;

import java.util.List;

/**
 * 描述：workflow节点
 *
 * @author zhilong.deng@hand-china.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNode {
    private String id;
    private Integer index;
    private String label;
    private String color;
    private String shape;
    /**
     * graph节点 自动生成的 无用 默认是node
     */
    private String type;
    private Double x;
    private Double y;
    private String size;

    /**
     * graph节点类型 包括job和flow
     */
    private String nodeType;
    /**
     * 任务类型 比如 sql  datax  sqoop
     */
    private String jobType;
    private String objectId;
    private String priorityLevel;
}
