package com.swm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SwmStorage;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.InventoryByAreaVO;
import com.swm.common.vo.WasteDistributionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SwmStorageMapper extends BaseMapper<SwmStorage> {

    @Select("SELECT storage_area AS storageArea, " +
            "COALESCE(SUM(CASE WHEN storage_type = 1 THEN change_weight ELSE 0 END), 0) AS inboundTotal, " +
            "COALESCE(SUM(CASE WHEN storage_type = 2 THEN change_weight ELSE 0 END), 0) AS outboundTotal, " +
            "COALESCE(SUM(CASE WHEN storage_type = 1 THEN change_weight ELSE -change_weight END), 0) AS totalInventory " +
            "FROM swm_storage WHERE is_deleted = 0 GROUP BY storage_area ORDER BY totalInventory DESC")
    List<InventoryByAreaVO> selectInventoryByArea();

    @Select("SELECT c.category_name AS wasteCategoryName, " +
            "COALESCE(SUM(CASE WHEN s.storage_type = 1 THEN s.change_weight ELSE -s.change_weight END), 0) AS totalWeight, " +
            "COUNT(DISTINCT s.batch_no) AS batchCount " +
            "FROM swm_storage s " +
            "INNER JOIN swm_receiving r ON s.batch_no = r.batch_no AND r.is_deleted = 0 " +
            "INNER JOIN swm_waste_category c ON r.waste_category_id = c.id " +
            "WHERE s.is_deleted = 0 " +
            "GROUP BY c.category_name " +
            "ORDER BY totalWeight DESC")
    List<WasteDistributionVO> selectDistributionByCategory();

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
