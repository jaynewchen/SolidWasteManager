package com.swm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.dto.TestingDTO;
import com.swm.common.dto.TestingQueryDTO;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TestingVO;

import java.util.List;
import java.util.Map;

public interface TestingService {
    IPage<TestingVO> queryPage(TestingQueryDTO query);
    TestingVO getById(Long id);
    TestingVO create(TestingDTO dto, Long userId);
    TestingVO update(Long id, TestingDTO dto);
    void delete(Long id);
    TestingVO review(Long id, String opinion, Boolean qualified, Long reviewerId);
    List<BatchOptionVO> getAvailableBatches();
    List<Map<String, Object>> getActiveUsers();
    Map<String, Object> getStatistics(Integer testingType, String startDate, String endDate);
}
