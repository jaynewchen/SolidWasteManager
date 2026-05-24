package com.swm.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) FROM swm_receiving WHERE receive_date = CURRENT_DATE AND is_deleted = 0")
    Long countTodayReceiving();

    @Select("SELECT COALESCE(SUM(net_weight), 0) FROM swm_receiving WHERE receive_date >= CAST(FORMATDATETIME(CURRENT_DATE, 'yyyy-MM-01') AS DATE) AND is_deleted = 0")
    Long sumMonthNetWeight();

    @Select("SELECT COUNT(*) FROM swm_receiving WHERE status IN (1, 2) AND is_deleted = 0")
    Long countPendingDisposal();

    @Select("SELECT COUNT(*) FROM swm_testing WHERE status = 3 AND is_deleted = 0")
    Long countPendingCheck();
}
