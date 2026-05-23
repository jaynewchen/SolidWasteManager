package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferVO {
    private Long id;
    private String transferNo;
    private String batchNo;
    private String plateNumber;
    private String vehicleRegNo;
    private Long driverId;
    private String driverName;
    private String plannedRoute;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime transferTime;
    private BigDecimal transferWeight;
    private LocalDateTime loadingTime;
    private Long shippingConfirmerId;
    private String shippingConfirmerName;
    private LocalDateTime arrivalTime;
    private BigDecimal arrivalGrossWeight;
    private Long arrivalConfirmerId;
    private String arrivalConfirmerName;
    private LocalDateTime unloadingTime;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private Integer status;
    private String statusDesc;
    private String producerName;
    private String wasteCategoryName;
    private BigDecimal netWeight;
    private LocalDateTime createTime;
}
