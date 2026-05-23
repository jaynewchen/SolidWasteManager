package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WasteDistributionVO {
    private String wasteCategoryName;
    private BigDecimal totalWeight;
    private Integer batchCount;
}
