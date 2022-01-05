package com.hand.along.dispatch.common.infra.mapper;

import com.hand.along.dispatch.common.domain.JobExecution;
import com.hand.along.dispatch.common.domain.JobExecutionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobExecutionMapper {
    int countByExample(JobExecutionExample example);

    int deleteByExample(JobExecutionExample example);

    int deleteByPrimaryKey(Long jobExecutionId);

    int insert(JobExecution record);

    int insertSelective(JobExecution record);

    List<JobExecution> selectByExampleWithBLOBs(JobExecutionExample example);

    List<JobExecution> selectByExample(JobExecutionExample example);

    JobExecution selectByPrimaryKey(Long jobExecutionId);

    int updateByExampleSelective(@Param("record") JobExecution record, @Param("example") JobExecutionExample example);

    int updateByExampleWithBLOBs(@Param("record") JobExecution record, @Param("example") JobExecutionExample example);

    int updateByExample(@Param("record") JobExecution record, @Param("example") JobExecutionExample example);

    int updateByPrimaryKeySelective(JobExecution record);

    int updateByPrimaryKeyWithBLOBs(JobExecution record);

    int updateByPrimaryKey(JobExecution record);
}