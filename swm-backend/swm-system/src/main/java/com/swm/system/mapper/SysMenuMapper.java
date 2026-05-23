package com.swm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.id, m.parent_id, m.menu_name, m.menu_type, m.permission, " +
            "m.path, m.component, m.icon, m.sort_order, m.visible, m.status, " +
            "m.create_time, m.update_time, m.create_by, m.update_by, m.is_deleted " +
            "FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 AND m.is_deleted = 0 " +
            "ORDER BY m.sort_order")
    List<SysMenu> selectByUserId(Long userId);
}
