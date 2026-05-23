package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_storage")
public class SwmStorage extends BaseEntity {
    private String batchNo;
    private String storageArea;
    private Integer storageType;
    private BigDecimal changeWeight;
    private LocalDate operationDate;
    private String destination;
    private Long receiveRecordId;
    private Long operatorId;
    private String remark;
}
