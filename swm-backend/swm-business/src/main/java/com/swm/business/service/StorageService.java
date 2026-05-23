package com.swm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.dto.StorageDTO;
import com.swm.common.dto.StorageQueryDTO;
import com.swm.common.vo.*;

import java.util.List;

public interface StorageService {

    IPage<StorageVO> queryPage(StorageQueryDTO query);

    StorageVO getById(Long id);

    StorageVO create(StorageDTO dto, Long userId);

    StorageVO update(Long id, StorageDTO dto);

    void delete(Long id);

    List<InventoryByAreaVO> getInventoryByArea();

    List<WasteDistributionVO> getDistributionByCategory();

    List<BatchOptionVO> getAvailableBatches();
}
