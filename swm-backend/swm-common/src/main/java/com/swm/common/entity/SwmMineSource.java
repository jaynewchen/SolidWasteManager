package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_mine_source")
public class SwmMineSource extends BaseEntity {
    private String mineCode;
    private String mineName;
    private Integer status;
}
