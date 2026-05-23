package com.swm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swm.common.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT id, username, password, real_name, phone, email, avatar, status, " +
            "last_login_time, last_login_ip, create_time, update_time, create_by, update_by, is_deleted " +
            "FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    SysUser selectByUsername(String username);
}
