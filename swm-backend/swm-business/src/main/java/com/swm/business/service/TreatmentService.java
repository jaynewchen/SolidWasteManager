package com.swm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.dto.TreatmentDTO;
import com.swm.common.dto.TreatmentQueryDTO;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TreatmentVO;

import java.util.List;

public interface TreatmentService {

    IPage<TreatmentVO> queryPage(TreatmentQueryDTO query);

    TreatmentVO getById(Long id);

    TreatmentVO create(TreatmentDTO dto, Long userId);

    TreatmentVO update(Long id, TreatmentDTO dto);

    void delete(Long id);

    List<BatchOptionVO> getAvailableBatches();
}
