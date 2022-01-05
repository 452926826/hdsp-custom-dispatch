package com.hand.along.dispatch.master.infra.mapper;

import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.master.domain.WorkflowExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WorkflowMapper {
    int countByExample(WorkflowExample example);

    int deleteByExample(WorkflowExample example);

    int deleteByPrimaryKey(Long workflowId);

    int insert(Workflow record);

    int insertSelective(Workflow record);

    List<Workflow> selectByExampleWithBLOBs(WorkflowExample example);

    List<Workflow> selectByExample(WorkflowExample example);

    Workflow selectByPrimaryKey(Long workflowId);

    int updateByExampleSelective(@Param("record") Workflow record, @Param("example") WorkflowExample example);

    int updateByExampleWithBLOBs(@Param("record") Workflow record, @Param("example") WorkflowExample example);

    int updateByExample(@Param("record") Workflow record, @Param("example") WorkflowExample example);

    int updateByPrimaryKeySelective(Workflow record);

    int updateByPrimaryKeyWithBLOBs(Workflow record);

    int updateByPrimaryKey(Workflow record);
}