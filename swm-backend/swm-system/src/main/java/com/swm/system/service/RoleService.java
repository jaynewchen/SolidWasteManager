package com.swm.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.common.dto.RoleDTO;
import com.swm.common.dto.RoleQueryDTO;
import com.swm.common.entity.SysMenu;
import com.swm.common.entity.SysRole;
import com.swm.common.vo.RoleVO;

import java.util.List;

public interface RoleService {

    IPage<RoleVO> queryPage(RoleQueryDTO query);

    RoleVO getById(Long id);

    RoleVO create(RoleDTO dto);

    RoleVO update(Long id, RoleDTO dto);

    void delete(Long id);

    List<SysRole> getAll();

    List<SysMenu> getMenuTree();
}
