package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_testing")
public class SwmTesting extends BaseEntity {
    private String testingNo;
    private Integer testingType;
    private String batchNo;
    private Long receivingId;
    private Long treatmentId;
    private String planName;
    private LocalDateTime testingDate;
    private LocalDateTime plannedDate;
    private String sampleName;
    private String testingItem;
    private String testingMethod;
    private String testingStandard;
    private String standardValue;
    private String testingValue;
    private String testingResult;
    private Integer isQualified;
    private Long testerId;
    private Long planExecutorId;
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private String reviewOpinion;
    private String remark;
    private Integer status;
}
