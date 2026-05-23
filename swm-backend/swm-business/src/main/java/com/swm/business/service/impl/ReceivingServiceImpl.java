package com.swm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.business.mapper.SwmReceivingMapper;
import com.swm.business.service.BatchNoGenerator;
import com.swm.business.service.ReceivingService;
import com.swm.common.dto.ReceivingDTO;
import com.swm.common.dto.ReceivingQueryDTO;
import com.swm.common.entity.*;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.ReceivingVO;
import com.swm.dict.mapper.SwmMineSourceMapper;
import com.swm.dict.mapper.SwmWasteCategoryMapper;
import com.swm.dict.mapper.SwmWasteProducerMapper;
import com.swm.dict.mapper.SwmWorkshopMapper;
import com.swm.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReceivingServiceImpl implements ReceivingService {

    @Autowired
    private SwmReceivingMapper swmReceivingMapper;

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWorkshopMapper workshopMapper;

    @Autowired
    private SwmMineSourceMapper mineSourceMapper;

    @Autowired
    private SwmWasteCategoryMapper wasteCategoryMapper;

    @Autowired
    private BatchNoGenerator batchNoGenerator;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<ReceivingVO> queryPage(ReceivingQueryDTO query) {
        Page<SwmReceiving> page = new Page<SwmReceiving>(query.getPage(), query.getSize());

        QueryWrapper<SwmReceiving> wrapper = new QueryWrapper<SwmReceiving>();
        if (StringUtils.hasText(query.getBatchNo())) {
            wrapper.like("batch_no", query.getBatchNo());
        }
        if (query.getProducerId() != null) {
            wrapper.eq("producer_id", query.getProducerId());
        }
        if (query.getWasteCategoryId() != null) {
            wrapper.eq("waste_category_id", query.getWasteCategoryId());
        }
        if (StringUtils.hasText(query.getPlateNumber())) {
            wrapper.like("plate_number", query.getPlateNumber());
        }
        if (query.getDriverId() != null) {
            wrapper.eq("driver_id", query.getDriverId());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge("receive_date", query.getStartDate());
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le("receive_date", query.getEndDate());
        }

        if (StringUtils.hasText(query.getSortField())) {
            String order = "desc".equalsIgnoreCase(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        } else {
            wrapper.orderByDesc("create_time");
        }

        IPage<SwmReceiving> resultPage = swmReceivingMapper.selectPage(page, wrapper);

        return resultPage.convert(this::convertToVO);
    }

    @Override
    public ReceivingVO getById(Long id) {
        SwmReceiving receiving = swmReceivingMapper.selectById(id);
        if (receiving == null) {
            throw new BusinessException(40000, "接收记录不存在");
        }
        return convertToVO(receiving);
    }

    @Override
    public ReceivingVO create(ReceivingDTO dto, Long userId) {
        validateReceivingDTO(dto);

        SwmReceiving receiving = new SwmReceiving();

        receiving.setProducerId(dto.getProducerId());
        receiving.setWorkshopId(dto.getWorkshopId());
        receiving.setMineSourceId(dto.getMineSourceId());
        receiving.setWasteCategoryId(dto.getWasteCategoryId());
        receiving.setPlateNumber(dto.getPlateNumber());
        receiving.setDriverId(dto.getDriverId());
        receiving.setGrossWeight(dto.getGrossWeight());
        receiving.setTareWeight(dto.getTareWeight());

        BigDecimal netWeight = dto.getGrossWeight().subtract(dto.getTareWeight());
        receiving.setNetWeight(netWeight);

        LocalDate receiveDate = LocalDate.now();
        if (StringUtils.hasText(dto.getReceiveDate())) {
            receiveDate = LocalDate.parse(dto.getReceiveDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        receiving.setReceiveDate(receiveDate);

        LocalTime receiveTime = LocalTime.now();
        if (StringUtils.hasText(dto.getReceiveTime())) {
            receiveTime = LocalTime.parse(dto.getReceiveTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        receiving.setReceiveTime(receiveTime);

        receiving.setReceiveUserId(userId);
        receiving.setRemark(dto.getRemark());
        receiving.setStatus(1);

        // Generate batch number
        String batchNo = batchNoGenerator.generate(dto.getProducerId(), dto.getWorkshopId(),
                dto.getMineSourceId(), receiveDate);
        receiving.setBatchNo(batchNo);

        swmReceivingMapper.insert(receiving);

        return convertToVO(receiving);
    }

    @Override
    public ReceivingVO update(Long id, ReceivingDTO dto) {
        SwmReceiving existing = swmReceivingMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "接收记录不存在");
        }
        if (existing.getStatus() == null || existing.getStatus() != 1) {
            throw new BusinessException(40000, "只能修改待处理状态的记录");
        }

        validateReceivingDTO(dto);

        existing.setProducerId(dto.getProducerId());
        existing.setWorkshopId(dto.getWorkshopId());
        existing.setMineSourceId(dto.getMineSourceId());
        existing.setWasteCategoryId(dto.getWasteCategoryId());
        existing.setPlateNumber(dto.getPlateNumber());
        existing.setDriverId(dto.getDriverId());
        existing.setGrossWeight(dto.getGrossWeight());
        existing.setTareWeight(dto.getTareWeight());

        BigDecimal netWeight = dto.getGrossWeight().subtract(dto.getTareWeight());
        existing.setNetWeight(netWeight);

        if (StringUtils.hasText(dto.getReceiveDate())) {
            existing.setReceiveDate(LocalDate.parse(dto.getReceiveDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (StringUtils.hasText(dto.getReceiveTime())) {
            existing.setReceiveTime(LocalTime.parse(dto.getReceiveTime(),
                    DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
        existing.setRemark(dto.getRemark());

        swmReceivingMapper.updateById(existing);

        return convertToVO(existing);
    }

    @Override
    public void delete(Long id) {
        SwmReceiving existing = swmReceivingMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "接收记录不存在");
        }
        swmReceivingMapper.deleteById(id);
    }

    private void validateReceivingDTO(ReceivingDTO dto) {
        if (dto.getGrossWeight() == null || dto.getTareWeight() == null) {
            throw new BusinessException(40000, "毛重和皮重不能为空");
        }
        BigDecimal netWeight = dto.getGrossWeight().subtract(dto.getTareWeight());
        if (netWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(40000, "净重必须大于0");
        }
        if (dto.getGrossWeight().compareTo(dto.getTareWeight()) <= 0) {
            throw new BusinessException(40000, "毛重必须大于皮重");
        }
        if (StringUtils.hasText(dto.getReceiveDate())) {
            LocalDate receiveDate = LocalDate.parse(dto.getReceiveDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (receiveDate.isAfter(LocalDate.now())) {
                throw new BusinessException(40000, "接收日期不能是未来日期");
            }
        }
    }

    private ReceivingVO convertToVO(SwmReceiving receiving) {
        ReceivingVO vo = new ReceivingVO();
        BeanUtils.copyProperties(receiving, vo);

        // Fill dictionary names
        if (receiving.getProducerId() != null) {
            SwmWasteProducer producer = wasteProducerMapper.selectById(receiving.getProducerId());
            if (producer != null) {
                vo.setProducerName(producer.getProducerName());
            }
        }
        if (receiving.getWorkshopId() != null) {
            SwmWorkshop workshop = workshopMapper.selectById(receiving.getWorkshopId());
            if (workshop != null) {
                vo.setWorkshopName(workshop.getWorkshopName());
            }
        }
        if (receiving.getMineSourceId() != null) {
            SwmMineSource mine = mineSourceMapper.selectById(receiving.getMineSourceId());
            if (mine != null) {
                vo.setMineSourceName(mine.getMineName());
            }
        }
        if (receiving.getWasteCategoryId() != null) {
            SwmWasteCategory category = wasteCategoryMapper.selectById(receiving.getWasteCategoryId());
            if (category != null) {
                vo.setWasteCategoryName(category.getCategoryName());
            }
        }

        // Status description
        if (receiving.getStatus() != null) {
            switch (receiving.getStatus()) {
                case 1: vo.setStatusDesc("已接收"); break;
                case 2: vo.setStatusDesc("处置中"); break;
                case 3: vo.setStatusDesc("已处置"); break;
                case 4: vo.setStatusDesc("已完结"); break;
                default: vo.setStatusDesc("未知"); break;
            }
        }

        // Fill user names
        if (receiving.getDriverId() != null) {
            SysUser driver = sysUserMapper.selectById(receiving.getDriverId());
            if (driver != null) {
                vo.setDriverName(driver.getRealName());
            }
        }
        if (receiving.getReceiveUserId() != null) {
            SysUser receiver = sysUserMapper.selectById(receiving.getReceiveUserId());
            if (receiver != null) {
                vo.setReceiveUserName(receiver.getRealName());
            }
        }

        return vo;
    }
}
