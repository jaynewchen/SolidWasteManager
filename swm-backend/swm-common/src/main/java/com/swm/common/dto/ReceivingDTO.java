package com.swm.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceivingDTO {
    private Long producerId;
    private Long workshopId;
    private Long mineSourceId;
    private Long wasteCategoryId;
    private String plateNumber;
    private Long driverId;
    private BigDecimal grossWeight;
    private BigDecimal tareWeight;
    private String receiveDate;
    private String receiveTime;
    private String remark;
}
