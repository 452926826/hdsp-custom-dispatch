package com.hand.along.dispatch.common.infra.mapper;

import com.hand.along.dispatch.common.domain.ExecutionLog;
import com.hand.along.dispatch.common.domain.ExecutionLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExecutionLogMapper {
    int countByExample(ExecutionLogExample example);

    int deleteByExample(ExecutionLogExample example);

    int deleteByPrimaryKey(Long logId);

    int insert(ExecutionLog record);

    int insertSelective(ExecutionLog record);

    List<ExecutionLog> selectByExampleWithBLOBs(ExecutionLogExample example);

    List<ExecutionLog> selectByExample(ExecutionLogExample example);

    ExecutionLog selectByPrimaryKey(Long logId);

    int updateByExampleSelective(@Param("record") ExecutionLog record, @Param("example") ExecutionLogExample example);

    int updateByExampleWithBLOBs(@Param("record") ExecutionLog record, @Param("example") ExecutionLogExample example);

    int updateByExample(@Param("record") ExecutionLog record, @Param("example") ExecutionLogExample example);

    int updateByPrimaryKeySelective(ExecutionLog record);

    int updateByPrimaryKeyWithBLOBs(ExecutionLog record);

    int updateByPrimaryKey(ExecutionLog record);
}