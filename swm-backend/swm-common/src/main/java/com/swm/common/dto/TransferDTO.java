package com.swm.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {
    private String batchNo;
    private String plateNumber;
    private String vehicleRegNo;
    private Long driverId;
    private String plannedRoute;
    private String fromLocation;
    private String toLocation;
    private String transferTime;
    private BigDecimal transferWeight;
    private String loadingTime;
    private Long shippingConfirmerId;
    private String arrivalTime;
    private BigDecimal arrivalGrossWeight;
    private Long arrivalConfirmerId;
    private String unloadingTime;
    private String remark;
    private Integer status;
}
