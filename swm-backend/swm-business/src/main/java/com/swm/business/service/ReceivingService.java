package com.swm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.dto.ReceivingDTO;
import com.swm.common.dto.ReceivingQueryDTO;
import com.swm.common.vo.ReceivingVO;

public interface ReceivingService {

    IPage<ReceivingVO> queryPage(ReceivingQueryDTO query);

    ReceivingVO getById(Long id);

    ReceivingVO create(ReceivingDTO dto, Long userId);

    ReceivingVO update(Long id, ReceivingDTO dto);

    void delete(Long id);
}
