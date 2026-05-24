package com.swm.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryDTO extends PageDTO {

    private String roleCode;

    private String roleName;

    private Integer status;
}
