package com.swm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.business.mapper.SwmReceivingMapper;
import com.swm.business.mapper.SwmTestingMapper;
import com.swm.business.service.TestingService;
import com.swm.common.dto.TestingDTO;
import com.swm.common.dto.TestingQueryDTO;
import com.swm.common.entity.SwmReceiving;
import com.swm.common.entity.SwmTesting;
import com.swm.common.entity.SysUser;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TestingVO;
import com.swm.dict.mapper.SwmWasteCategoryMapper;
import com.swm.dict.mapper.SwmWasteProducerMapper;
import com.swm.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestingServiceImpl implements TestingService {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private SwmTestingMapper swmTestingMapper;

    @Autowired
    private SwmReceivingMapper swmReceivingMapper;

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWasteCategoryMapper wasteCategoryMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<TestingVO> queryPage(TestingQueryDTO query) {
        Page<SwmTesting> page = new Page<>(query.getPage(), query.getSize());

        QueryWrapper<SwmTesting> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getBatchNo())) {
            wrapper.like("batch_no", query.getBatchNo());
        }
        if (StringUtils.hasText(query.getTestingNo())) {
            wrapper.like("testing_no", query.getTestingNo());
        }
        if (query.getTestingType() != null) {
            wrapper.eq("testing_type", query.getTestingType());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge("testing_date", query.getStartDate());
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le("testing_date", query.getEndDate());
        }

        if (StringUtils.hasText(query.getSortField())) {
            String order = "desc".equalsIgnoreCase(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        } else {
            wrapper.orderByDesc("create_time");
        }

        IPage<SwmTesting> resultPage = swmTestingMapper.selectPage(page, wrapper);
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public TestingVO getById(Long id) {
        SwmTesting testing = swmTestingMapper.selectById(id);
        if (testing == null) {
            throw new BusinessException(40000, "检测记录不存在");
        }
        return convertToVO(testing);
    }

    @Override
    public TestingVO create(TestingDTO dto, Long userId) {
        validateTestingDTO(dto, null);

        SwmTesting testing = new SwmTesting();
        testing.setTestingNo(generateTestingNo());
        testing.setTestingType(dto.getTestingType());
        testing.setBatchNo(dto.getBatchNo());
        testing.setReceivingId(dto.getReceivingId());
        testing.setTreatmentId(dto.getTreatmentId());
        testing.setPlanName(dto.getPlanName());
        testing.setSampleName(dto.getSampleName());
        testing.setTestingItem(dto.getTestingItem());
        testing.setTestingMethod(dto.getTestingMethod());
        testing.setTestingStandard(dto.getTestingStandard());
        testing.setStandardValue(dto.getStandardValue());
        testing.setTestingValue(dto.getTestingValue());
        testing.setTestingResult(dto.getTestingResult());
        testing.setTesterId(dto.getTesterId());
        testing.setPlanExecutorId(dto.getPlanExecutorId());
        testing.setRemark(dto.getRemark());

        if (dto.getTestingType() != null && dto.getTestingType() == 1) {
            testing.setStatus(1);
        } else {
            testing.setStatus(1);
        }

        if (StringUtils.hasText(dto.getTestingDate())) {
            testing.setTestingDate(LocalDateTime.parse(dto.getTestingDate() + " 00:00:00", DATETIME_FMT));
        }
        if (StringUtils.hasText(dto.getPlannedDate())) {
            testing.setPlannedDate(LocalDateTime.parse(dto.getPlannedDate() + " 00:00:00", DATETIME_FMT));
        }

        swmTestingMapper.insert(testing);
        return convertToVO(testing);
    }

    @Override
    public TestingVO update(Long id, TestingDTO dto) {
        SwmTesting existing = swmTestingMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "检测记录不存在");
        }

        validateTestingDTO(dto, id);

        Integer oldStatus = existing.getStatus();
        Integer newStatus = dto.getStatus();

        if (newStatus != null && !newStatus.equals(oldStatus)) {
            validateStatusProgression(existing.getTestingType(), oldStatus, newStatus);
            existing.setStatus(newStatus);
        }

        existing.setTestingType(dto.getTestingType());
        existing.setBatchNo(dto.getBatchNo());
        existing.setReceivingId(dto.getReceivingId());
        existing.setTreatmentId(dto.getTreatmentId());
        existing.setPlanName(dto.getPlanName());
        existing.setSampleName(dto.getSampleName());
        existing.setTestingItem(dto.getTestingItem());
        existing.setTestingMethod(dto.getTestingMethod());
        existing.setTestingStandard(dto.getTestingStandard());
        existing.setStandardValue(dto.getStandardValue());
        existing.setTestingValue(dto.getTestingValue());
        existing.setTestingResult(dto.getTestingResult());
        existing.setTesterId(dto.getTesterId());
        existing.setPlanExecutorId(dto.getPlanExecutorId());
        existing.setRemark(dto.getRemark());

        if (dto.getIsQualified() != null) {
            existing.setIsQualified(dto.getIsQualified());
        }

        if (StringUtils.hasText(dto.getTestingDate())) {
            existing.setTestingDate(LocalDateTime.parse(dto.getTestingDate() + " 00:00:00", DATETIME_FMT));
        } else {
            existing.setTestingDate(null);
        }
        if (StringUtils.hasText(dto.getPlannedDate())) {
            existing.setPlannedDate(LocalDateTime.parse(dto.getPlannedDate() + " 00:00:00", DATETIME_FMT));
        } else {
            existing.setPlannedDate(null);
        }

        if (StringUtils.hasText(dto.getReviewTime())) {
            existing.setReviewTime(LocalDateTime.parse(dto.getReviewTime(), DATETIME_FMT));
        }

        swmTestingMapper.updateById(existing);
        return convertToVO(existing);
    }

    @Override
    public void delete(Long id) {
        SwmTesting existing = swmTestingMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "检测记录不存在");
        }
        swmTestingMapper.deleteById(id);
    }

    @Override
    public TestingVO review(Long id, String opinion, Boolean qualified, Long reviewerId) {
        SwmTesting existing = swmTestingMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "检测记录不存在");
        }
        if (existing.getTestingType() == 1) {
            throw new BusinessException(40000, "检测计划无需复核");
        }
        if (existing.getStatus() == null || existing.getStatus() != 3) {
            throw new BusinessException(40000, "只有待复核状态的记录才能复核");
        }

        existing.setStatus(4);
        existing.setReviewOpinion(opinion);
        existing.setIsQualified(qualified != null && qualified ? 1 : 0);
        existing.setReviewerId(reviewerId);
        existing.setReviewTime(LocalDateTime.now());

        swmTestingMapper.updateById(existing);
        return convertToVO(existing);
    }

    @Override
    public List<BatchOptionVO> getAvailableBatches() {
        return swmTestingMapper.selectAvailableBatches();
    }

    @Override
    public List<Map<String, Object>> getActiveUsers() {
        return swmTestingMapper.selectActiveUsers();
    }

    @Override
    public Map<String, Object> getStatistics(Integer testingType, String startDate, String endDate) {
        QueryWrapper<SwmTesting> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        if (testingType != null) {
            wrapper.eq("testing_type", testingType);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge("testing_date", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le("testing_date", endDate);
        }

        List<SwmTesting> list = swmTestingMapper.selectList(wrapper);
        long totalCount = list.size();
        long qualifiedCount = list.stream().filter(t -> t.getIsQualified() != null && t.getIsQualified() == 1).count();
        long unqualifiedCount = list.stream().filter(t -> t.getIsQualified() != null && t.getIsQualified() == 0).count();

        Map<Integer, Long> byType = new HashMap<>();
        Map<Integer, Long> byStatus = new HashMap<>();
        for (SwmTesting t : list) {
            byType.merge(t.getTestingType(), 1L, Long::sum);
            byStatus.merge(t.getStatus(), 1L, Long::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("qualifiedCount", qualifiedCount);
        result.put("unqualifiedCount", unqualifiedCount);
        result.put("qualifyRate", totalCount > 0 ? (double) qualifiedCount / totalCount * 100 : 0);
        result.put("byType", byType);
        result.put("byStatus", byStatus);
        return result;
    }

    private String generateTestingNo() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "JC" + today;
        String maxNo = swmTestingMapper.selectMaxTestingNoByPrefix(prefix);
        if (maxNo == null) {
            return prefix + "001";
        }
        int seq = Integer.parseInt(maxNo.substring(maxNo.length() - 3)) + 1;
        return prefix + String.format("%03d", seq);
    }

    private void validateTestingDTO(TestingDTO dto, Long excludeId) {
        if (dto.getTestingType() == null) {
            throw new BusinessException(40000, "检测类型不能为空");
        }
        int type = dto.getTestingType();
        if (type < 1 || type > 4) {
            throw new BusinessException(40000, "检测类型无效");
        }

        if (type == 1) {
            if (!StringUtils.hasText(dto.getPlanName())) {
                throw new BusinessException(40000, "计划名称不能为空");
            }
            if (!StringUtils.hasText(dto.getPlannedDate())) {
                throw new BusinessException(40000, "计划检测日期不能为空");
            }
            if (dto.getPlanExecutorId() == null) {
                throw new BusinessException(40000, "计划执行人不能为空");
            }
            if (dto.getPlanExecutorId() != null) {
                SysUser user = sysUserMapper.selectById(dto.getPlanExecutorId());
                if (user == null) {
                    throw new BusinessException(40000, "计划执行人不存在");
                }
            }
        } else {
            if (!StringUtils.hasText(dto.getBatchNo())) {
                throw new BusinessException(40000, "批次号不能为空");
            }
            if (!StringUtils.hasText(dto.getTestingItem())) {
                throw new BusinessException(40000, "检测项目不能为空");
            }
            if (dto.getTesterId() == null) {
                throw new BusinessException(40000, "检测员不能为空");
            }

            SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(dto.getBatchNo());
            if (receiving == null) {
                throw new BusinessException(40000, "批次号不存在");
            }

            if (dto.getTesterId() != null) {
                SysUser tester = sysUserMapper.selectById(dto.getTesterId());
                if (tester == null) {
                    throw new BusinessException(40000, "检测员不存在");
                }
            }
        }

        if (dto.getStatus() != null) {
            int status = dto.getStatus();
            if (type == 1) {
                if (status < 1 || status > 3) {
                    throw new BusinessException(40000, "计划状态值无效");
                }
            } else {
                if (status < 1 || status > 4) {
                    throw new BusinessException(40000, "检测状态值无效");
                }
            }
        }
    }

    private void validateStatusProgression(Integer testingType, Integer oldStatus, Integer newStatus) {
        if (newStatus <= oldStatus) {
            throw new BusinessException(40000, "状态不能回退");
        }
        int maxStatus = (testingType != null && testingType == 1) ? 3 : 4;
        if (newStatus > maxStatus) {
            throw new BusinessException(40000, "状态值无效");
        }
        if (newStatus - oldStatus > 1) {
            throw new BusinessException(40000, "状态不能跳过，需按顺序推进");
        }
    }

    private TestingVO convertToVO(SwmTesting testing) {
        TestingVO vo = new TestingVO();
        BeanUtils.copyProperties(testing, vo);

        // Testing type description
        if (testing.getTestingType() != null) {
            switch (testing.getTestingType()) {
                case 1: vo.setTestingTypeDesc("检测计划"); break;
                case 2: vo.setTestingTypeDesc("进厂物料检测"); break;
                case 3: vo.setTestingTypeDesc("处理过程检测"); break;
                case 4: vo.setTestingTypeDesc("产物质量检测"); break;
            }
        }

        // Status description
        boolean isPlan = testing.getTestingType() != null && testing.getTestingType() == 1;
        int status = testing.getStatus() != null ? testing.getStatus() : 1;
        if (isPlan) {
            switch (status) {
                case 1: vo.setStatusDesc("待执行"); break;
                case 2: vo.setStatusDesc("执行中"); break;
                case 3: vo.setStatusDesc("已完成"); break;
                default: vo.setStatusDesc("未知");
            }
        } else {
            switch (status) {
                case 1: vo.setStatusDesc("待检测"); break;
                case 2: vo.setStatusDesc("检测中"); break;
                case 3: vo.setStatusDesc("待复核"); break;
                case 4: vo.setStatusDesc("已完成"); break;
                default: vo.setStatusDesc("未知");
            }
        }

        // Operator name
        if (testing.getCreateBy() != null) {
            SysUser operator = sysUserMapper.selectById(testing.getCreateBy());
            if (operator != null) {
                vo.setOperatorName(operator.getRealName());
            }
        }

        // Tester name
        if (testing.getTesterId() != null) {
            SysUser tester = sysUserMapper.selectById(testing.getTesterId());
            if (tester != null) {
                vo.setTesterName(tester.getRealName());
            }
        }

        // Plan executor name
        if (testing.getPlanExecutorId() != null) {
            SysUser executor = sysUserMapper.selectById(testing.getPlanExecutorId());
            if (executor != null) {
                vo.setPlanExecutorName(executor.getRealName());
            }
        }

        // Reviewer name
        if (testing.getReviewerId() != null) {
            SysUser reviewer = sysUserMapper.selectById(testing.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getRealName());
            }
        }

        // Lookup receiving record by batch_no
        if (StringUtils.hasText(testing.getBatchNo())) {
            SwmReceiving receiving = swmReceivingMapper.selectOneByBatchNo(testing.getBatchNo());
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
        }

        return vo;
    }
}
