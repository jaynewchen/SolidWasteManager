package com.swm.system.service.impl;

import com.swm.common.entity.SysMenu;
import com.swm.common.entity.SysRole;
import com.swm.common.entity.SysUser;
import com.swm.common.vo.UserInfoVO;
import com.swm.system.mapper.SysMenuMapper;
import com.swm.system.mapper.SysRoleMapper;
import com.swm.system.mapper.SysUserMapper;
import com.swm.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        List<SysRole> roles = sysRoleMapper.selectByUserId(userId);
        List<SysMenu> allMenus = sysMenuMapper.selectByUserId(userId);

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());

        // roles
        List<String> roleCodes = new ArrayList<String>();
        for (SysRole role : roles) {
            if (role.getRoleCode() != null) {
                roleCodes.add(role.getRoleCode());
            }
        }
        vo.setRoles(roleCodes);

        // permissions (all menu types: M/C/B)
        List<String> permissions = new ArrayList<String>();
        for (SysMenu menu : allMenus) {
            if (menu.getPermission() != null && menu.getPermission().length() > 0) {
                permissions.add(menu.getPermission());
            }
        }
        vo.setPermissions(permissions);

        // menu tree (non-button, visible)
        List<SysMenu> menuList = new ArrayList<SysMenu>();
        for (SysMenu menu : allMenus) {
            if (!"B".equals(menu.getMenuType()) && menu.getVisible() != null
                    && menu.getVisible() == 1) {
                menuList.add(menu);
            }
        }
        List<SysMenu> menuTree = buildMenuTree(menuList);
        vo.setMenus(menuTree);

        return vo;
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menuList) {
        List<SysMenu> tree = new ArrayList<SysMenu>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                tree.add(findChildren(menu, menuList));
            }
        }
        return tree;
    }

    private SysMenu findChildren(SysMenu parent, List<SysMenu> menuList) {
        List<SysMenu> children = new ArrayList<SysMenu>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId() != null && menu.getParentId().equals(parent.getId())) {
                children.add(findChildren(menu, menuList));
            }
        }
        parent.setChildren(children);
        return parent;
    }
}
