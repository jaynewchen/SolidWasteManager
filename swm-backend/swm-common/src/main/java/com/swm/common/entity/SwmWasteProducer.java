package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("swm_waste_producer")
public class SwmWasteProducer extends BaseEntity {
    private String producerCode;
    private String producerName;
    private String licenseNo;
    private String licenseSuffix;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private Integer status;
}
