package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    private Long parentId;
    private String menuName;
    private String menuType;
    private String permission;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
    private Integer status;

    private transient List<SysMenu> children;
}
