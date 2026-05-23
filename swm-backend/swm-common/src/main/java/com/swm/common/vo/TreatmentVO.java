package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TreatmentVO {
    private Long id;
    private String batchNo;
    private Long processId;
    private String processName;
    private LocalDate treatmentDate;
    private BigDecimal inputWeight;
    private BigDecimal outputWeight;
    private BigDecimal treatmentLoss;
    private BigDecimal temperature;
    private String equipmentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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
