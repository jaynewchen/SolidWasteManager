package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_treatment_process")
public class SwmTreatmentProcess extends BaseEntity {
    private String processCode;
    private String processName;
    private Integer status;
}
