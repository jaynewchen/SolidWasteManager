package com.swm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.business.mapper.SwmTreatmentMapper;
import com.swm.business.service.TreatmentService;
import com.swm.common.dto.TreatmentDTO;
import com.swm.common.dto.TreatmentQueryDTO;
import com.swm.common.entity.*;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TreatmentVO;
import com.swm.dict.mapper.SwmTreatmentProcessMapper;
import com.swm.business.mapper.SwmReceivingMapper;
import com.swm.dict.mapper.SwmWasteCategoryMapper;
import com.swm.dict.mapper.SwmWasteProducerMapper;
import com.swm.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TreatmentServiceImpl implements TreatmentService {

    @Autowired
    private SwmTreatmentMapper swmTreatmentMapper;

    @Autowired
    private SwmReceivingMapper swmReceivingMapper;

    @Autowired
    private SwmTreatmentProcessMapper treatmentProcessMapper;

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWasteCategoryMapper wasteCategoryMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<TreatmentVO> queryPage(TreatmentQueryDTO query) {
        Page<SwmTreatment> page = new Page<>(query.getPage(), query.getSize());

        QueryWrapper<SwmTreatment> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getBatchNo())) {
            wrapper.like("batch_no", query.getBatchNo());
        }
        if (query.getProcessId() != null) {
            wrapper.eq("process_id", query.getProcessId());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge("treatment_date", query.getStartDate());
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le("treatment_date", query.getEndDate());
        }

        if (StringUtils.hasText(query.getSortField())) {
            String order = "desc".equalsIgnoreCase(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        } else {
            wrapper.orderByDesc("create_time");
        }

        IPage<SwmTreatment> resultPage = swmTreatmentMapper.selectPage(page, wrapper);
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public TreatmentVO getById(Long id) {
        SwmTreatment treatment = swmTreatmentMapper.selectById(id);
        if (treatment == null) {
            throw new BusinessException(40000, "处置记录不存在");
        }
        return convertToVO(treatment);
    }

    @Override
    public TreatmentVO create(TreatmentDTO dto, Long userId) {
        validateTreatmentDTO(dto, null);

        SwmTreatment treatment = new SwmTreatment();
        treatment.setBatchNo(dto.getBatchNo());
        treatment.setProcessId(dto.getProcessId());
        treatment.setInputWeight(dto.getInputWeight());
        treatment.setOutputWeight(dto.getOutputWeight());
        treatment.setTreatmentLoss(dto.getInputWeight().subtract(dto.getOutputWeight()));
        treatment.setTemperature(dto.getTemperature());
        treatment.setEquipmentName(dto.getEquipmentName());
        treatment.setOperatorId(userId);
        treatment.setRemark(dto.getRemark());
        treatment.setStatus(1);

        if (StringUtils.hasText(dto.getTreatmentDate())) {
            treatment.setTreatmentDate(LocalDate.parse(dto.getTreatmentDate(), DATE_FMT));
        } else {
            treatment.setTreatmentDate(LocalDate.now());
        }
        if (StringUtils.hasText(dto.getStartTime())) {
            treatment.setStartTime(LocalDateTime.parse(dto.getStartTime(), DATETIME_FMT));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            treatment.setEndTime(LocalDateTime.parse(dto.getEndTime(), DATETIME_FMT));
        }

        swmTreatmentMapper.insert(treatment);
        return convertToVO(treatment);
    }

    @Override
    public TreatmentVO update(Long id, TreatmentDTO dto) {
        SwmTreatment existing = swmTreatmentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "处置记录不存在");
        }

        validateTreatmentDTO(dto, id);

        existing.setBatchNo(dto.getBatchNo());
        existing.setProcessId(dto.getProcessId());
        existing.setInputWeight(dto.getInputWeight());
        existing.setOutputWeight(dto.getOutputWeight());
        existing.setTreatmentLoss(dto.getInputWeight().subtract(dto.getOutputWeight()));
        existing.setTemperature(dto.getTemperature());
        existing.setEquipmentName(dto.getEquipmentName());
        existing.setRemark(dto.getRemark());

        if (StringUtils.hasText(dto.getTreatmentDate())) {
            existing.setTreatmentDate(LocalDate.parse(dto.getTreatmentDate(), DATE_FMT));
        }
        if (StringUtils.hasText(dto.getStartTime())) {
            existing.setStartTime(LocalDateTime.parse(dto.getStartTime(), DATETIME_FMT));
        } else {
            existing.setStartTime(null);
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            existing.setEndTime(LocalDateTime.parse(dto.getEndTime(), DATETIME_FMT));
        } else {
            existing.setEndTime(null);
        }

        swmTreatmentMapper.updateById(existing);
        return convertToVO(existing);
    }

    @Override
    public void delete(Long id) {
        SwmTreatment existing = swmTreatmentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "处置记录不存在");
        }
        swmTreatmentMapper.deleteById(id);
    }

    @Override
    public List<BatchOptionVO> getAvailableBatches() {
        return swmTreatmentMapper.selectAvailableBatches();
    }

    private void validateTreatmentDTO(TreatmentDTO dto, Long excludeId) {
        if (!StringUtils.hasText(dto.getBatchNo())) {
            throw new BusinessException(40000, "批次号不能为空");
        }
        if (dto.getProcessId() == null) {
            throw new BusinessException(40000, "处置工艺不能为空");
        }
        if (dto.getInputWeight() == null || dto.getInputWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(40000, "入炉重量必须大于0");
        }
        if (dto.getOutputWeight() == null || dto.getOutputWeight().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(40000, "产出重量不能小于0");
        }

        SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(dto.getBatchNo());
        if (receiving == null) {
            throw new BusinessException(40000, "批次号不存在");
        }

        SwmTreatmentProcess process = treatmentProcessMapper.selectById(dto.getProcessId());
        if (process == null) {
            throw new BusinessException(40000, "处置工艺不存在");
        }

        if (StringUtils.hasText(dto.getTreatmentDate())) {
            LocalDate treatmentDate = LocalDate.parse(dto.getTreatmentDate(), DATE_FMT);
            if (treatmentDate.isAfter(LocalDate.now())) {
                throw new BusinessException(40000, "处置日期不能是未来日期");
            }
        }

        if (StringUtils.hasText(dto.getStartTime()) && StringUtils.hasText(dto.getEndTime())) {
            LocalDateTime start = LocalDateTime.parse(dto.getStartTime(), DATETIME_FMT);
            LocalDateTime end = LocalDateTime.parse(dto.getEndTime(), DATETIME_FMT);
            if (start.isAfter(end)) {
                throw new BusinessException(40000, "开始时间不能晚于结束时间");
            }
        }
    }

    private TreatmentVO convertToVO(SwmTreatment treatment) {
        TreatmentVO vo = new TreatmentVO();
        BeanUtils.copyProperties(treatment, vo);

        if (treatment.getStatus() != null) {
            vo.setStatusDesc(treatment.getStatus() == 1 ? "进行中" : "已完成");
        }

        if (treatment.getOperatorId() != null) {
            SysUser operator = sysUserMapper.selectById(treatment.getOperatorId());
            if (operator != null) {
                vo.setOperatorName(operator.getRealName());
            }
        }

        if (treatment.getProcessId() != null) {
            SwmTreatmentProcess process = treatmentProcessMapper.selectById(treatment.getProcessId());
            if (process != null) {
                vo.setProcessName(process.getProcessName());
            }
        }

        SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(treatment.getBatchNo());
        if (receiving != null) {
            vo.setNetWeight(receiving.getNetWeight());
            if (receiving.getWasteCategoryId() != null) {
                SwmWasteCategory category = wasteCategoryMapper.selectById(receiving.getWasteCategoryId());
                if (category != null) {
                    vo.setWasteCategoryName(category.getCategoryName());
                }
            }
            if (receiving.getProducerId() != null) {
                SwmWasteProducer producer = wasteProducerMapper.selectById(receiving.getProducerId());
                if (producer != null) {
                    vo.setProducerName(producer.getProducerName());
                }
            }
        }

        return vo;
    }
}
