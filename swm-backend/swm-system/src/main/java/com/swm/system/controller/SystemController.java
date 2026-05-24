package com.swm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.Result;
import com.swm.common.entity.SysUser;
import com.swm.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SysUser>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<SysUser> p = new Page<>(page, size);
        p = sysUserMapper.selectPage(p, new QueryWrapper<SysUser>().orderByDesc("create_time"));
        // Clear password before returning
        for (SysUser user : p.getRecords()) {
            user.setPassword(null);
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
        user.setPassword(null);
        return Result.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteUser(@PathVariable Long id) {
        sysUserMapper.deleteById(id);
        return Result.ok();
    }
}
