package com.swm.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StorageDTO {
    private String batchNo;
    private String storageArea;
    private Integer storageType;
    private BigDecimal changeWeight;
    private String operationDate;
    private String destination;
    private Long receiveRecordId;
    private String remark;
}
