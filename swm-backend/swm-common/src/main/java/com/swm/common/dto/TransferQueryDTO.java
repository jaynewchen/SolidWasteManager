package com.swm.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransferQueryDTO extends PageDTO {
    private String batchNo;
    private String transferNo;
    private Integer status;
    private String startDate;
    private String endDate;
}
