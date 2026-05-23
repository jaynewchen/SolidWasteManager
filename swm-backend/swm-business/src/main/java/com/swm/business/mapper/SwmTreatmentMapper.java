package com.swm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SwmTreatment;
import com.swm.common.vo.BatchOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SwmTreatmentMapper extends BaseMapper<SwmTreatment> {

    @Select("SELECT r.batch_no AS batchNo, r.id AS receiveRecordId, " +
            "p.producer_name AS producerName, c.category_name AS wasteCategoryName, " +
            "r.net_weight AS netWeight " +
            "FROM swm_receiving r " +
            "LEFT JOIN swm_waste_producer p ON r.producer_id = p.id " +
            "LEFT JOIN swm_waste_category c ON r.waste_category_id = c.id " +
            "WHERE r.is_deleted = 0 AND r.batch_no IS NOT NULL " +
            "ORDER BY r.create_time DESC")
    List<BatchOptionVO> selectAvailableBatches();
}
