package com.swm.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {

    private String roleCode;

    private String roleName;

    private String description;

    private Integer status;

    private List<Long> menuIds;
}
