package com.swm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT r.id, r.role_code, r.role_name, r.description, r.status, " +
            "r.create_time, r.update_time, r.create_by, r.update_by, r.is_deleted " +
            "FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = 0")
    List<SysRole> selectByUserId(Long userId);
}
