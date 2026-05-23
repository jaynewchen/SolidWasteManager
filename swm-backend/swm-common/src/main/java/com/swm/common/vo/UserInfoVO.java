package com.swm.common.vo;

import com.swm.common.entity.SysMenu;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private String phone;
    private List<String> roles;
    private List<String> permissions;
    private List<SysMenu> menus;
}
