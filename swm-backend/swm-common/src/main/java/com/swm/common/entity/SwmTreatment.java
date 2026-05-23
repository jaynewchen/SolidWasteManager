package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_treatment")
public class SwmTreatment extends BaseEntity {
    private String batchNo;
    private Long processId;
    private LocalDate treatmentDate;
    private BigDecimal inputWeight;
    private BigDecimal outputWeight;
    private BigDecimal treatmentLoss;
    private BigDecimal temperature;
    private String equipmentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long operatorId;
    private String remark;
    private Integer status;
}
