package com.swm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.dto.TransferDTO;
import com.swm.common.dto.TransferQueryDTO;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TransferVO;

import java.util.List;
import java.util.Map;

public interface TransferService {
    IPage<TransferVO> queryPage(TransferQueryDTO query);
    TransferVO getById(Long id);
    TransferVO create(TransferDTO dto, Long userId);
    TransferVO update(Long id, TransferDTO dto);
    void delete(Long id);
    List<BatchOptionVO> getAvailableBatches();
    List<Map<String, Object>> getActiveUsers();
}
