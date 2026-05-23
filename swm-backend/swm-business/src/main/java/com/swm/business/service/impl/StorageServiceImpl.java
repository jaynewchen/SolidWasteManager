package com.swm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.business.mapper.SwmReceivingMapper;
import com.swm.business.mapper.SwmStorageMapper;
import com.swm.business.service.StorageService;
import com.swm.common.dto.StorageDTO;
import com.swm.common.dto.StorageQueryDTO;
import com.swm.common.entity.*;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.*;
import com.swm.dict.mapper.SwmWasteCategoryMapper;
import com.swm.dict.mapper.SwmWasteProducerMapper;
import com.swm.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private SwmStorageMapper swmStorageMapper;

    @Autowired
    private SwmReceivingMapper swmReceivingMapper;

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWasteCategoryMapper wasteCategoryMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<StorageVO> queryPage(StorageQueryDTO query) {
        Page<SwmStorage> page = new Page<>(query.getPage(), query.getSize());

        QueryWrapper<SwmStorage> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getBatchNo())) {
            wrapper.like("batch_no", query.getBatchNo());
        }
        if (StringUtils.hasText(query.getStorageArea())) {
            wrapper.like("storage_area", query.getStorageArea());
        }
        if (query.getStorageType() != null) {
            wrapper.eq("storage_type", query.getStorageType());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge("operation_date", query.getStartDate());
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le("operation_date", query.getEndDate());
        }

        if (StringUtils.hasText(query.getSortField())) {
            String order = "desc".equalsIgnoreCase(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        } else {
            wrapper.orderByDesc("create_time");
        }

        IPage<SwmStorage> resultPage = swmStorageMapper.selectPage(page, wrapper);
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public StorageVO getById(Long id) {
        SwmStorage storage = swmStorageMapper.selectById(id);
        if (storage == null) {
            throw new BusinessException(40000, "贮存记录不存在");
        }
        return convertToVO(storage);
    }

    @Override
    public StorageVO create(StorageDTO dto, Long userId) {
        validateStorageDTO(dto, null);

        SwmStorage storage = new SwmStorage();
        storage.setBatchNo(dto.getBatchNo());
        storage.setStorageArea(dto.getStorageArea());
        storage.setStorageType(dto.getStorageType());
        storage.setChangeWeight(dto.getChangeWeight());
        storage.setDestination(dto.getDestination());
        storage.setReceiveRecordId(dto.getReceiveRecordId());
        storage.setOperatorId(userId);
        storage.setRemark(dto.getRemark());

        LocalDate operationDate = LocalDate.now();
        if (StringUtils.hasText(dto.getOperationDate())) {
            operationDate = LocalDate.parse(dto.getOperationDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        storage.setOperationDate(operationDate);

        swmStorageMapper.insert(storage);
        return convertToVO(storage);
    }

    @Override
    public StorageVO update(Long id, StorageDTO dto) {
        SwmStorage existing = swmStorageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "贮存记录不存在");
        }

        validateStorageDTO(dto, id);

        existing.setBatchNo(dto.getBatchNo());
        existing.setStorageArea(dto.getStorageArea());
        existing.setStorageType(dto.getStorageType());
        existing.setChangeWeight(dto.getChangeWeight());
        existing.setDestination(dto.getDestination());
        existing.setReceiveRecordId(dto.getReceiveRecordId());
        existing.setRemark(dto.getRemark());

        if (StringUtils.hasText(dto.getOperationDate())) {
            existing.setOperationDate(LocalDate.parse(dto.getOperationDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        swmStorageMapper.updateById(existing);
        return convertToVO(existing);
    }

    @Override
    public void delete(Long id) {
        SwmStorage existing = swmStorageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "贮存记录不存在");
        }
        swmStorageMapper.deleteById(id);
    }

    @Override
    public List<InventoryByAreaVO> getInventoryByArea() {
        return swmStorageMapper.selectInventoryByArea();
    }

    @Override
    public List<WasteDistributionVO> getDistributionByCategory() {
        return swmStorageMapper.selectDistributionByCategory();
    }

    @Override
    public List<BatchOptionVO> getAvailableBatches() {
        return swmStorageMapper.selectAvailableBatches();
    }

    private void validateStorageDTO(StorageDTO dto, Long excludeId) {
        if (!StringUtils.hasText(dto.getBatchNo())) {
            throw new BusinessException(40000, "批次号不能为空");
        }
        if (!StringUtils.hasText(dto.getStorageArea())) {
            throw new BusinessException(40000, "存放库区不能为空");
        }
        if (dto.getChangeWeight() == null || dto.getChangeWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(40000, "重量必须大于0");
        }
        if (dto.getStorageType() == null || (dto.getStorageType() != 1 && dto.getStorageType() != 2)) {
            throw new BusinessException(40000, "操作类型无效");
        }

        // Verify batch exists
        SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(dto.getBatchNo());
        if (receiving == null) {
            throw new BusinessException(40000, "批次号不存在");
        }

        if (dto.getStorageType() == 2) {
            // Outbound: destination required
            if (!StringUtils.hasText(dto.getDestination())) {
                throw new BusinessException(40000, "出库去向不能为空");
            }
            // Outbound: cannot exceed current inventory for this batch
            BigDecimal currentInventory = getBatchInventory(dto.getBatchNo(), excludeId);
            if (dto.getChangeWeight().compareTo(currentInventory) > 0) {
                throw new BusinessException(40000, "出库量不能超过当前库存量");
            }
        }

        if (dto.getStorageType() == 1) {
            // Inbound: total inbound for this batch cannot exceed receiving net_weight
            BigDecimal totalInbound = getBatchTotalInbound(dto.getBatchNo(), excludeId);
            BigDecimal newTotal = totalInbound.add(dto.getChangeWeight());
            if (receiving.getNetWeight() != null && newTotal.compareTo(receiving.getNetWeight()) > 0) {
                throw new BusinessException(40000, "入库总量不能超过接收净重(" + receiving.getNetWeight() + "吨)");
            }
        }

        if (StringUtils.hasText(dto.getOperationDate())) {
            LocalDate operationDate = LocalDate.parse(dto.getOperationDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (operationDate.isAfter(LocalDate.now())) {
                throw new BusinessException(40000, "操作日期不能是未来日期");
            }
        }
    }

    /**
     * Compute current net inventory for a batch (inbound - outbound),
     * optionally excluding a record (for update validation).
     */
    private BigDecimal getBatchInventory(String batchNo, Long excludeId) {
        // Use the receiving's net_weight as reference instead of querying storage
        // This is more reliable: inbound total for this batch cannot exceed receiving net_weight
        // outbound total for this batch cannot exceed inbound total
        // We compute: sum of inbound - sum of outbound for this batch
        QueryWrapper<SwmStorage> wrapper = new QueryWrapper<>();
        wrapper.eq("batch_no", batchNo);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        List<SwmStorage> records = swmStorageMapper.selectList(wrapper);
        BigDecimal inbound = BigDecimal.ZERO;
        BigDecimal outbound = BigDecimal.ZERO;
        for (SwmStorage r : records) {
            if (r.getStorageType() == 1) {
                inbound = inbound.add(r.getChangeWeight());
            } else if (r.getStorageType() == 2) {
                outbound = outbound.add(r.getChangeWeight());
            }
        }
        return inbound.subtract(outbound);
    }

    /**
     * Get total inbound weight for a batch (excluding a record ID for update).
     */
    private BigDecimal getBatchTotalInbound(String batchNo, Long excludeId) {
        QueryWrapper<SwmStorage> wrapper = new QueryWrapper<>();
        wrapper.eq("batch_no", batchNo);
        wrapper.eq("storage_type", 1);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        List<SwmStorage> records = swmStorageMapper.selectList(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (SwmStorage r : records) {
            total = total.add(r.getChangeWeight());
        }
        return total;
    }

    private StorageVO convertToVO(SwmStorage storage) {
        StorageVO vo = new StorageVO();
        BeanUtils.copyProperties(storage, vo);

        if (storage.getStorageType() != null) {
            vo.setStorageTypeDesc(storage.getStorageType() == 1 ? "入库" : "出库");
        }

        // Operator name
        if (storage.getOperatorId() != null) {
            SysUser operator = sysUserMapper.selectById(storage.getOperatorId());
            if (operator != null) {
                vo.setOperatorName(operator.getRealName());
            }
        }

        // Lookup receiving record by batch_no for waste category and producer
        SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(storage.getBatchNo());
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
