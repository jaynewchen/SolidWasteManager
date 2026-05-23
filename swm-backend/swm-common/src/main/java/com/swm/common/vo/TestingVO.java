package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TestingVO {
    private Long id;
    private String testingNo;
    private Integer testingType;
    private String testingTypeDesc;
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
    private String testerName;
    private Long planExecutorId;
    private String planExecutorName;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime reviewTime;
    private String reviewOpinion;
    private String remark;
    private Integer status;
    private String statusDesc;
    private String producerName;
    private String wasteCategoryName;
    private BigDecimal netWeight;
    private Long createBy;
    private String operatorName;
    private LocalDateTime createTime;
}
