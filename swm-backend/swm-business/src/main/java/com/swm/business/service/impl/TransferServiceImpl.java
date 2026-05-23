package com.swm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.business.mapper.SwmReceivingMapper;
import com.swm.business.mapper.SwmTransferMapper;
import com.swm.business.service.TransferService;
import com.swm.common.dto.TransferDTO;
import com.swm.common.dto.TransferQueryDTO;
import com.swm.common.entity.SwmReceiving;
import com.swm.common.entity.SwmTransfer;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TransferVO;
import com.swm.dict.mapper.SwmWasteCategoryMapper;
import com.swm.dict.mapper.SwmWasteProducerMapper;
import com.swm.system.mapper.SysUserMapper;
import com.swm.common.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class TransferServiceImpl implements TransferService {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SwmTransferMapper swmTransferMapper;

    @Autowired
    private SwmReceivingMapper swmReceivingMapper;

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWasteCategoryMapper wasteCategoryMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<TransferVO> queryPage(TransferQueryDTO query) {
        Page<SwmTransfer> page = new Page<>(query.getPage(), query.getSize());

        QueryWrapper<SwmTransfer> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getBatchNo())) {
            wrapper.like("batch_no", query.getBatchNo());
        }
        if (StringUtils.hasText(query.getTransferNo())) {
            wrapper.like("transfer_no", query.getTransferNo());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge("transfer_time", query.getStartDate());
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le("transfer_time", query.getEndDate());
        }

        if (StringUtils.hasText(query.getSortField())) {
            String order = "desc".equalsIgnoreCase(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        } else {
            wrapper.orderByDesc("create_time");
        }

        IPage<SwmTransfer> resultPage = swmTransferMapper.selectPage(page, wrapper);
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public TransferVO getById(Long id) {
        SwmTransfer transfer = swmTransferMapper.selectById(id);
        if (transfer == null) {
            throw new BusinessException(40000, "转运记录不存在");
        }
        return convertToVO(transfer);
    }

    @Override
    public TransferVO create(TransferDTO dto, Long userId) {
        validateTransferDTO(dto, null);

        SwmTransfer transfer = new SwmTransfer();
        transfer.setTransferNo(generateTransferNo());
        transfer.setBatchNo(dto.getBatchNo());
        transfer.setPlateNumber(dto.getPlateNumber());
        transfer.setVehicleRegNo(dto.getVehicleRegNo());
        transfer.setDriverId(dto.getDriverId());
        transfer.setPlannedRoute(dto.getPlannedRoute());
        transfer.setFromLocation(dto.getFromLocation());
        transfer.setToLocation(dto.getToLocation());
        transfer.setTransferWeight(dto.getTransferWeight());
        transfer.setArrivalGrossWeight(dto.getArrivalGrossWeight());
        transfer.setShippingConfirmerId(dto.getShippingConfirmerId());
        transfer.setArrivalConfirmerId(dto.getArrivalConfirmerId());
        transfer.setOperatorId(userId);
        transfer.setRemark(dto.getRemark());
        transfer.setStatus(1);

        if (StringUtils.hasText(dto.getTransferTime())) {
            transfer.setTransferTime(LocalDateTime.parse(dto.getTransferTime(), DATETIME_FMT));
        }
        if (StringUtils.hasText(dto.getLoadingTime())) {
            transfer.setLoadingTime(LocalDateTime.parse(dto.getLoadingTime(), DATETIME_FMT));
        }
        if (StringUtils.hasText(dto.getArrivalTime())) {
            transfer.setArrivalTime(LocalDateTime.parse(dto.getArrivalTime(), DATETIME_FMT));
        }
        if (StringUtils.hasText(dto.getUnloadingTime())) {
            transfer.setUnloadingTime(LocalDateTime.parse(dto.getUnloadingTime(), DATETIME_FMT));
        }

        swmTransferMapper.insert(transfer);
        return convertToVO(transfer);
    }

    @Override
    public TransferVO update(Long id, TransferDTO dto) {
        SwmTransfer existing = swmTransferMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "转运记录不存在");
        }

        validateTransferDTO(dto, id);

        existing.setBatchNo(dto.getBatchNo());
        existing.setPlateNumber(dto.getPlateNumber());
        existing.setVehicleRegNo(dto.getVehicleRegNo());
        existing.setDriverId(dto.getDriverId());
        existing.setPlannedRoute(dto.getPlannedRoute());
        existing.setFromLocation(dto.getFromLocation());
        existing.setToLocation(dto.getToLocation());
        existing.setTransferWeight(dto.getTransferWeight());
        existing.setArrivalGrossWeight(dto.getArrivalGrossWeight());
        existing.setShippingConfirmerId(dto.getShippingConfirmerId());
        existing.setArrivalConfirmerId(dto.getArrivalConfirmerId());
        existing.setRemark(dto.getRemark());

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        existing.setTransferTime(parseDateTime(dto.getTransferTime()));
        existing.setLoadingTime(parseDateTime(dto.getLoadingTime()));
        existing.setArrivalTime(parseDateTime(dto.getArrivalTime()));
        existing.setUnloadingTime(parseDateTime(dto.getUnloadingTime()));

        swmTransferMapper.updateById(existing);
        return convertToVO(existing);
    }

    @Override
    public void delete(Long id) {
        SwmTransfer existing = swmTransferMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "转运记录不存在");
        }
        swmTransferMapper.deleteById(id);
    }

    @Override
    public List<BatchOptionVO> getAvailableBatches() {
        return swmTransferMapper.selectAvailableBatches();
    }

    @Override
    public List<Map<String, Object>> getActiveUsers() {
        return swmTransferMapper.selectActiveUsers();
    }

    private String generateTransferNo() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "ZY" + today;
        String maxNo = swmTransferMapper.selectMaxTransferNoByPrefix(prefix);
        if (maxNo == null) {
            return prefix + "001";
        }
        int seq = Integer.parseInt(maxNo.substring(maxNo.length() - 3)) + 1;
        return prefix + String.format("%03d", seq);
    }

    private void validateTransferDTO(TransferDTO dto, Long excludeId) {
        if (!StringUtils.hasText(dto.getBatchNo())) {
            throw new BusinessException(40000, "批次号不能为空");
        }
        if (!StringUtils.hasText(dto.getPlateNumber())) {
            throw new BusinessException(40000, "车牌号不能为空");
        }
        if (dto.getDriverId() == null) {
            throw new BusinessException(40000, "驾驶员不能为空");
        }
        if (!StringUtils.hasText(dto.getFromLocation())) {
            throw new BusinessException(40000, "起运地不能为空");
        }
        if (!StringUtils.hasText(dto.getToLocation())) {
            throw new BusinessException(40000, "目的地不能为空");
        }
        if (dto.getTransferWeight() == null || dto.getTransferWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(40000, "转运重量必须大于0");
        }
        if (dto.getArrivalGrossWeight() != null && dto.getArrivalGrossWeight().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(40000, "到达毛重不能为负数");
        }

        // Verify batch exists
        SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(dto.getBatchNo());
        if (receiving == null) {
            throw new BusinessException(40000, "批次号不存在");
        }

        // Verify driver exists
        if (dto.getDriverId() != null) {
            SysUser driver = sysUserMapper.selectById(dto.getDriverId());
            if (driver == null) {
                throw new BusinessException(40000, "驾驶员不存在");
            }
        }

        // Time sequence validation
        LocalDateTime transferTime = parseDateTime(dto.getTransferTime());
        LocalDateTime loadingTime = parseDateTime(dto.getLoadingTime());
        LocalDateTime arrivalTime = parseDateTime(dto.getArrivalTime());
        LocalDateTime unloadingTime = parseDateTime(dto.getUnloadingTime());

        if (loadingTime != null && transferTime != null && loadingTime.isAfter(transferTime)) {
            throw new BusinessException(40000, "装车时间不能晚于转运时间");
        }
        if (arrivalTime != null && transferTime != null && arrivalTime.isBefore(transferTime)) {
            throw new BusinessException(40000, "到达时间不能早于转运时间");
        }
        if (unloadingTime != null && arrivalTime != null && unloadingTime.isBefore(arrivalTime)) {
            throw new BusinessException(40000, "卸车时间不能早于到达时间");
        }

        if (dto.getStatus() != null && (dto.getStatus() < 1 || dto.getStatus() > 4)) {
            throw new BusinessException(40000, "状态值无效");
        }
    }

    private LocalDateTime parseDateTime(String str) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        return LocalDateTime.parse(str, DATETIME_FMT);
    }

    private TransferVO convertToVO(SwmTransfer transfer) {
        TransferVO vo = new TransferVO();
        BeanUtils.copyProperties(transfer, vo);

        // Status description
        switch (transfer.getStatus() != null ? transfer.getStatus() : 1) {
            case 1: vo.setStatusDesc("待装车"); break;
            case 2: vo.setStatusDesc("运输中"); break;
            case 3: vo.setStatusDesc("已到达"); break;
            case 4: vo.setStatusDesc("已卸车"); break;
            default: vo.setStatusDesc("未知");
        }

        // Operator name
        if (transfer.getOperatorId() != null) {
            SysUser operator = sysUserMapper.selectById(transfer.getOperatorId());
            if (operator != null) {
                vo.setOperatorName(operator.getRealName());
            }
        }

        // Driver name
        if (transfer.getDriverId() != null) {
            SysUser driver = sysUserMapper.selectById(transfer.getDriverId());
            if (driver != null) {
                vo.setDriverName(driver.getRealName());
            }
        }

        // Shipping confirmer name
        if (transfer.getShippingConfirmerId() != null) {
            SysUser confirmer = sysUserMapper.selectById(transfer.getShippingConfirmerId());
            if (confirmer != null) {
                vo.setShippingConfirmerName(confirmer.getRealName());
            }
        }

        // Arrival confirmer name
        if (transfer.getArrivalConfirmerId() != null) {
            SysUser confirmer = sysUserMapper.selectById(transfer.getArrivalConfirmerId());
            if (confirmer != null) {
                vo.setArrivalConfirmerName(confirmer.getRealName());
            }
        }

        // Lookup receiving record by batch_no for waste category and producer
        SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(transfer.getBatchNo());
        if (receiving != null) {
            vo.setNetWeight(receiving.getNetWeight());
            if (receiving.getWasteCategoryId() != null) {
                com.swm.common.entity.SwmWasteCategory category = wasteCategoryMapper.selectById(receiving.getWasteCategoryId());
                if (category != null) {
                    vo.setWasteCategoryName(category.getCategoryName());
                }
            }
            if (receiving.getProducerId() != null) {
                com.swm.common.entity.SwmWasteProducer producer = wasteProducerMapper.selectById(receiving.getProducerId());
                if (producer != null) {
                    vo.setProducerName(producer.getProducerName());
                }
            }
        }

        return vo;
    }
}
