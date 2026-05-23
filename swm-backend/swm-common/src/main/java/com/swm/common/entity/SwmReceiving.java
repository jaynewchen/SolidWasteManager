package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_receiving")
public class SwmReceiving extends BaseEntity {
    private String batchNo;
    private Long producerId;
    private Long workshopId;
    private Long mineSourceId;
    private Long wasteCategoryId;
    private String plateNumber;
    private Long driverId;
    private BigDecimal grossWeight;
    private BigDecimal tareWeight;
    private BigDecimal netWeight;
    private LocalDate receiveDate;
    private LocalTime receiveTime;
    private Long receiveUserId;
    private String remark;
    private Integer status;
}
