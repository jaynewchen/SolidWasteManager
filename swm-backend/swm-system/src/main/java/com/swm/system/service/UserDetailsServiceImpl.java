package com.swm.system.service;

import com.swm.common.entity.SysMenu;
import com.swm.common.entity.SysRole;
import com.swm.common.entity.SysUser;
import com.swm.system.mapper.SysMenuMapper;
import com.swm.system.mapper.SysRoleMapper;
import com.swm.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<SysRole> roles = sysRoleMapper.selectByUserId(sysUser.getId());
        List<SysMenu> menus = sysMenuMapper.selectByUserId(sysUser.getId());

        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        // Add role authorities
        for (SysRole role : roles) {
            if (role.getRoleCode() != null && role.getRoleCode().length() > 0) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
            }
        }

        // Add menu permission authorities
        for (SysMenu menu : menus) {
            if (menu.getPermission() != null && menu.getPermission().length() > 0) {
                authorities.add(new SimpleGrantedAuthority(menu.getPermission()));
            }
        }

        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                sysUser.getStatus() == null || sysUser.getStatus() == 1,
                true,
                true,
                true,
                authorities
        );
    }
}
