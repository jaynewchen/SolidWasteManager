package com.swm.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReceivingQueryDTO extends PageDTO {
    private String batchNo;
    private Long producerId;
    private Long wasteCategoryId;
    private String plateNumber;
    private Long driverId;
    private Integer status;
    private String startDate;
    private String endDate;
}
