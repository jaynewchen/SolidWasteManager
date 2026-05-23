package com.swm.system.service;

import com.swm.common.entity.SysUser;
import com.swm.common.vo.UserInfoVO;

public interface SysUserService {

    SysUser getByUsername(String username);

    UserInfoVO getUserInfo(Long userId);
}
