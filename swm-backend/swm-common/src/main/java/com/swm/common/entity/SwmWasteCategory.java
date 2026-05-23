package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_waste_category")
public class SwmWasteCategory extends BaseEntity {
    private String categoryCode;
    private String categoryName;
    private Integer status;
}
