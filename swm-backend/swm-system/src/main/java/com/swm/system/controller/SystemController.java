package com.swm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.Result;
import com.swm.common.entity.SysRole;
import com.swm.common.entity.SysUser;
import com.swm.common.entity.SysUserRole;
import com.swm.system.mapper.SysRoleMapper;
import com.swm.system.mapper.SysUserMapper;
import com.swm.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('system:user:list','receiving:list','transfer:list','storage:list','treatment:list','testing:list')")
    public Result<Page<SysUser>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<SysUser> p = new Page<>(page, size);
        p = sysUserMapper.selectPage(p, new QueryWrapper<SysUser>().orderByDesc("create_time"));
        for (SysUser user : p.getRecords()) {
            user.setPassword(null);
            // Populate role info
            List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                    new QueryWrapper<SysUserRole>().eq("user_id", user.getId()));
            List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            user.setRoleIds(roleIds);
            if (!roleIds.isEmpty()) {
                List<SysRole> roles = sysRoleMapper.selectList(
                        new QueryWrapper<SysRole>().in("id", roleIds));
                user.setRoles(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
            } else {
                user.setRoles(new ArrayList<>());
            }
        }
        return Result.ok(p);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> createUser(@RequestBody SysUser user) {
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        sysUserMapper.insert(user);
        // Save role assignments
        saveUserRoles(user.getId(), user.getRoleIds());
        user.setPassword(null);
        return Result.ok(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        // Don't update password if empty
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(null);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        sysUserMapper.updateById(user);
        // Update role assignments if provided
        if (user.getRoleIds() != null) {
            sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", id));
            saveUserRoles(id, user.getRoleIds());
        }
        user.setPassword(null);
        return Result.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteUser(@PathVariable Long id) {
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", id));
        sysUserMapper.deleteById(id);
        return Result.ok();
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }
}
