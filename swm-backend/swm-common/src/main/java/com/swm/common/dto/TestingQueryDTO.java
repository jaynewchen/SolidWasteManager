package com.swm.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestingQueryDTO extends PageDTO {
    private String batchNo;
    private String testingNo;
    private Integer testingType;
    private Integer status;
    private String startDate;
    private String endDate;
}
