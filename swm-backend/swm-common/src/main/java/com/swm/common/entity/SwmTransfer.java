package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_transfer")
public class SwmTransfer extends BaseEntity {
    private String transferNo;
    private String batchNo;
    private String plateNumber;
    private String vehicleRegNo;
    private Long driverId;
    private String plannedRoute;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime transferTime;
    private BigDecimal transferWeight;
    private LocalDateTime loadingTime;
    private Long shippingConfirmerId;
    private LocalDateTime arrivalTime;
    private BigDecimal arrivalGrossWeight;
    private Long arrivalConfirmerId;
    private LocalDateTime unloadingTime;
    private Long operatorId;
    private String remark;
    private Integer status;
}
