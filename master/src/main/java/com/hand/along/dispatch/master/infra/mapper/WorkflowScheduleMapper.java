package com.hand.along.dispatch.master.infra.mapper;

import com.hand.along.dispatch.master.domain.WorkflowSchedule;
import com.hand.along.dispatch.master.domain.WorkflowScheduleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WorkflowScheduleMapper {
    int countByExample(WorkflowScheduleExample example);

    int deleteByExample(WorkflowScheduleExample example);

    int deleteByPrimaryKey(Long scheduleId);

    int insert(WorkflowSchedule record);

    int insertSelective(WorkflowSchedule record);

    List<WorkflowSchedule> selectByExample(WorkflowScheduleExample example);

    WorkflowSchedule selectByPrimaryKey(Long scheduleId);

    int updateByExampleSelective(@Param("record") WorkflowSchedule record, @Param("example") WorkflowScheduleExample example);

    int updateByExample(@Param("record") WorkflowSchedule record, @Param("example") WorkflowScheduleExample example);

    int updateByPrimaryKeySelective(WorkflowSchedule record);

    int updateByPrimaryKey(WorkflowSchedule record);
}