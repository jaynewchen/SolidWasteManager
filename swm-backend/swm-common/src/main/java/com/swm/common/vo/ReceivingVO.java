package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReceivingVO {
    private Long id;
    private String batchNo;
    private Long producerId;
    private String producerName;
    private Long workshopId;
    private String workshopName;
    private Long mineSourceId;
    private String mineSourceName;
    private Long wasteCategoryId;
    private String wasteCategoryName;
    private String plateNumber;
    private Long driverId;
    private String driverName;
    private BigDecimal grossWeight;
    private BigDecimal tareWeight;
    private BigDecimal netWeight;
    private LocalDate receiveDate;
    private LocalTime receiveTime;
    private Long receiveUserId;
    private String receiveUserName;
    private String remark;
    private Integer status;
    private String statusDesc;
    private LocalDateTime createTime;
}
