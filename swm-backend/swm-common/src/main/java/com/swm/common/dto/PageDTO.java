package com.swm.common.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer page = 1;
    private Integer size = 20;
    private String sortField;
    private String sortOrder;
}
