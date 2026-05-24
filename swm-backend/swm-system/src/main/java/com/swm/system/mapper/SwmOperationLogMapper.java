package com.swm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SwmOperationLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SwmOperationLogMapper extends BaseMapper<SwmOperationLog> {

    @Select("SELECT COUNT(*) FROM swm_operation_log")
    Long countAll();

    @Delete("DELETE FROM swm_operation_log WHERE id IN (SELECT id FROM swm_operation_log ORDER BY create_time ASC LIMIT #{n})")
    int deleteOldest(int n);
}
