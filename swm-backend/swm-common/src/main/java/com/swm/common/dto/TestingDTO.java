package com.swm.common.dto;

import lombok.Data;

@Data
public class TestingDTO {
    private Integer testingType;
    private String batchNo;
    private Long receivingId;
    private Long treatmentId;
    private String planName;
    private String testingDate;
    private String plannedDate;
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
    private String reviewTime;
    private String reviewOpinion;
    private String remark;
    private Integer status;
}
