package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BatchOptionVO {
    private String batchNo;
    private Long receiveRecordId;
    private String producerName;
    private String wasteCategoryName;
    private BigDecimal netWeight;
}
