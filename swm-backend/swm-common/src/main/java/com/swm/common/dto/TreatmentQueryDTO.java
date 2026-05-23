package com.swm.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TreatmentQueryDTO extends PageDTO {
    private String batchNo;
    private Long processId;
    private Integer status;
    private String startDate;
    private String endDate;
}
