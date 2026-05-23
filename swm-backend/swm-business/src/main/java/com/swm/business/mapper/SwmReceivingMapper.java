package com.swm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SwmReceiving;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SwmReceivingMapper extends BaseMapper<SwmReceiving> {

    @Select("SELECT MAX(batch_no) FROM swm_receiving WHERE batch_no LIKE CONCAT(#{prefix}, '%') AND is_deleted = 0")
    String selectMaxBatchNoByPrefix(String prefix);

    @Select("SELECT * FROM swm_receiving WHERE batch_no = #{batchNo} AND is_deleted = 0 LIMIT 1")
    SwmReceiving selectOneByBatchNo(String batchNo);
}
