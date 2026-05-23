package com.swm.common.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryByAreaVO {
    private String storageArea;
    private BigDecimal totalInventory;
    private BigDecimal inboundTotal;
    private BigDecimal outboundTotal;
}
