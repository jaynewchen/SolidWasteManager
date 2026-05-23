package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StorageVO {
    private Long id;
    private String batchNo;
    private String storageArea;
    private Integer storageType;
    private String storageTypeDesc;
    private BigDecimal changeWeight;
    private LocalDate operationDate;
    private String destination;
    private Long receiveRecordId;
    private Long operatorId;
    private String operatorName;
    private String wasteCategoryName;
    private String producerName;
    private BigDecimal netWeight;
    private String remark;
    private LocalDateTime createTime;
}
