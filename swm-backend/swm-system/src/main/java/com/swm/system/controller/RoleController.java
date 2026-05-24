package com.swm.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.Result;
import com.swm.common.annotation.OperationLog;
import com.swm.common.dto.RoleDTO;
import com.swm.common.dto.RoleQueryDTO;
import com.swm.common.entity.SysMenu;
import com.swm.common.entity.SysRole;
import com.swm.common.vo.RoleVO;
import com.swm.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<IPage<RoleVO>> list(RoleQueryDTO query) {
        return Result.ok(roleService.queryPage(query));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('system:role:list', 'system:user:list')")
    public Result<List<SysRole>> listAll() {
        return Result.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<RoleVO> detail(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @OperationLog(module = "角色管理", operation = "新增角色", level = "IMPORTANT")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<RoleVO> create(@RequestBody RoleDTO dto) {
        return Result.ok(roleService.create(dto));
    }

    @OperationLog(module = "角色管理", operation = "编辑角色", level = "IMPORTANT")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<RoleVO> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        return Result.ok(roleService.update(id, dto));
    }

    @OperationLog(module = "角色管理", operation = "删除角色", level = "IMPORTANT")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok();
    }

    @GetMapping("/menus/tree")
    @PreAuthorize("hasAnyAuthority('system:role:list', 'system:role:add', 'system:role:edit')")
    public Result<List<SysMenu>> menuTree() {
        return Result.ok(roleService.getMenuTree());
    }
}
