package com.swm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.dto.RoleDTO;
import com.swm.common.dto.RoleQueryDTO;
import com.swm.common.entity.SysMenu;
import com.swm.common.entity.SysRole;
import com.swm.common.entity.SysRoleMenu;
import com.swm.common.entity.SysUserRole;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.RoleVO;
import com.swm.system.mapper.SysMenuMapper;
import com.swm.system.mapper.SysRoleMapper;
import com.swm.system.mapper.SysRoleMenuMapper;
import com.swm.system.mapper.SysUserRoleMapper;
import com.swm.system.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public IPage<RoleVO> queryPage(RoleQueryDTO query) {
        Page<SysRole> page = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getRoleCode())) {
            wrapper.like("role_code", query.getRoleCode());
        }
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like("role_name", query.getRoleName());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        wrapper.orderByDesc("create_time");
        IPage<SysRole> result = sysRoleMapper.selectPage(page, wrapper);
        return result.convert(this::convertToVO);
    }

    @Override
    public RoleVO getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(40000, "角色不存在");
        }
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        vo.setMenuIds(roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));

        vo.setUserCount(getUserCount(id));
        return vo;
    }

    @Override
    @Transactional
    public RoleVO create(RoleDTO dto) {
        validateRoleDTO(dto, null);

        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        sysRoleMapper.insert(role);

        saveRoleMenus(role.getId(), dto.getMenuIds());
        return getById(role.getId());
    }

    @Override
    @Transactional
    public RoleVO update(Long id, RoleDTO dto) {
        SysRole existing = sysRoleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(40000, "角色不存在");
        }
        validateRoleDTO(dto, id);

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        sysRoleMapper.updateById(existing);

        sysRoleMenuMapper.delete(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        saveRoleMenus(id, dto.getMenuIds());

        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(40000, "角色不存在");
        }

        Long count = sysUserRoleMapper.selectCount(
                new QueryWrapper<SysUserRole>().eq("role_id", id));
        if (count != null && count > 0) {
            throw new BusinessException(40000, "该角色下还有 " + count + " 个用户，无法删除");
        }

        sysRoleMenuMapper.delete(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        sysRoleMapper.deleteById(id);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleMapper.selectList(
                new QueryWrapper<SysRole>().eq("status", 1).orderByAsc("create_time"));
    }

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = sysMenuMapper.selectList(
                new QueryWrapper<SysMenu>().eq("status", 1).orderByAsc("sort_order"));
        return buildMenuTree(allMenus);
    }

    private void validateRoleDTO(RoleDTO dto, Long excludeId) {
        if (!StringUtils.hasText(dto.getRoleCode())) {
            throw new BusinessException(40000, "角色编码不能为空");
        }
        if (!StringUtils.hasText(dto.getRoleName())) {
            throw new BusinessException(40000, "角色名称不能为空");
        }
        QueryWrapper<SysRole> wrapper = new QueryWrapper<SysRole>().eq("role_code", dto.getRoleCode());
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        Long count = sysRoleMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(40000, "角色编码已存在");
        }
    }

    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                sysRoleMenuMapper.insert(rm);
            }
        }
    }

    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        vo.setUserCount(getUserCount(role.getId()));
        return vo;
    }

    private Integer getUserCount(Long roleId) {
        Long count = sysUserRoleMapper.selectCount(
                new QueryWrapper<SysUserRole>().eq("role_id", roleId));
        return count != null ? count.intValue() : 0;
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menuList) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                tree.add(findChildren(menu, menuList));
            }
        }
        return tree;
    }

    private SysMenu findChildren(SysMenu parent, List<SysMenu> menuList) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId() != null && menu.getParentId().equals(parent.getId())) {
                children.add(findChildren(menu, menuList));
            }
        }
        parent.setChildren(children);
        return parent;
    }
}
