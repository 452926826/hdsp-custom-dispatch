package com.hand.along.dispatch.master.infra.mapper;

import com.hand.along.dispatch.master.domain.WorkflowExecution;
import com.hand.along.dispatch.master.domain.WorkflowExecutionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

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