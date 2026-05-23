package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_workshop")
public class SwmWorkshop extends BaseEntity {
    private String workshopCode;
    private String workshopAbbr;
    private String workshopName;
    private Integer status;
}
