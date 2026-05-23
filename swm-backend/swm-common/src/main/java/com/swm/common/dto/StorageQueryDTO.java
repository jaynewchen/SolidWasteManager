package com.swm.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageQueryDTO extends PageDTO {
    private String batchNo;
    private String storageArea;
    private Integer storageType;
    private String startDate;
    private String endDate;
}
