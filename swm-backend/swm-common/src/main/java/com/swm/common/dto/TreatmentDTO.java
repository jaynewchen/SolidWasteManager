package com.swm.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TreatmentDTO {
    private String batchNo;
    private Long processId;
    private String treatmentDate;
    private BigDecimal inputWeight;
    private BigDecimal outputWeight;
    private BigDecimal temperature;
    private String equipmentName;
    private String startTime;
    private String endTime;
    private String remark;
}
