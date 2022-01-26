package com.hand.along.dispatch.common.domain;

import lombok.*;
import org.springframework.data.annotation.Transient;

import java.util.*;

/**
 * 任务流运行中的临时任务状态节点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobNode extends BaseMessage{

    private String id;
    /**
     * 任务类型 workflow/job
     */
    private String nodeType;

    /**
     * 任务的类型
     */
    private String jobType;
    /**
     * id
     */
    private String objectId;
    /**
     * 优先级
     */
    private String priorityLevel;
    /**
     * 状态
     */
    private String status;
    /**
     * 日志
     */
    private List<String> logs = new ArrayList<>();

    /**
     * 提交时间
     */
    private Date submitDate;

    /**
     * 开始时间
     */
    private Date startDate;


    /**
     * 结束时间
     */
    private Date endDate;


    @Transient
    private String uniqueId;

    @Transient
    private Long workflowId;

    /**
     * 主要用于子任务流唯一标记
     */
    @Transient
    private String uuid;

    /**
     * 当前执行ID
     */
    @Transient
    private Long executionId;

    /**
     * 存放任务流参数
     */
    @Transient
    private Map<String,Object> globalParamMap;


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final JobNode node = (JobNode) o;

        return Objects.equals(id, node.getId()) && Objects.equals(objectId, node.getObjectId()) && Objects.equals(nodeType, node.getNodeType());

    }

    @Override
    public int hashCode() {
        return (id + objectId + nodeType).hashCode();
    }

}
