package com.hand.along.dispatch.common.infra.mapper;

import com.hand.along.dispatch.common.domain.WorkflowExecution;
import com.hand.along.dispatch.common.domain.WorkflowExecutionExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkflowExecutionMapper {
    int countByExample(WorkflowExecutionExample example);

    int deleteByExample(WorkflowExecutionExample example);

    int deleteByPrimaryKey(Long workflowExecutionId);

    int insert(WorkflowExecution record);

    int insertSelective(WorkflowExecution record);

    List<WorkflowExecution> selectByExampleWithBLOBs(WorkflowExecutionExample example);

    List<WorkflowExecution> selectByExample(WorkflowExecutionExample example);

    WorkflowExecution selectByPrimaryKey(Long workflowExecutionId);

    int updateByExampleSelective(@Param("record") WorkflowExecution record, @Param("example") WorkflowExecutionExample example);

    int updateByExampleWithBLOBs(@Param("record") WorkflowExecution record, @Param("example") WorkflowExecutionExample example);

    int updateByExample(@Param("record") WorkflowExecution record, @Param("example") WorkflowExecutionExample example);

    int updateByPrimaryKeySelective(WorkflowExecution record);

    int updateByPrimaryKeyWithBLOBs(WorkflowExecution record);

    int updateByPrimaryKey(WorkflowExecution record);
}