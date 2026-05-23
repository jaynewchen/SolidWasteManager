package com.swm.system.controller;

import com.swm.common.Result;
import com.swm.common.dto.LoginDTO;
import com.swm.common.exception.BusinessException;
import com.swm.common.vo.UserInfoVO;
import com.swm.security.utils.JwtUtils;
import com.swm.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserService sysUserService;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new BusinessException(40100, "用户名或密码错误");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        com.swm.common.entity.SysUser sysUser = sysUserService.getByUsername(loginDTO.getUsername());
        if (sysUser == null) {
            return Result.fail(40100, "用户名或密码错误");
        }

        // Get user roles
        UserInfoVO userInfo = sysUserService.getUserInfo(sysUser.getId());
        List<String> roles = userInfo != null && userInfo.getRoles() != null
                ? userInfo.getRoles() : new ArrayList<String>();
        List<String> permissions = userInfo != null && userInfo.getPermissions() != null
                ? userInfo.getPermissions() : new ArrayList<String>();

        // Generate token
        String token = jwtUtils.generateToken(sysUser.getId(), sysUser.getUsername(), roles, permissions);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("accessToken", token);
        data.put("tokenType", "Bearer");
        data.put("expiresIn", expiration);

        return Result.ok(data);
    }

    @GetMapping("/userinfo")
    public Result getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.fail(40100, "未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return Result.fail(40100, "未登录");
        }
        Long userId;
        if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            return Result.fail(40100, "未登录");
        }

        UserInfoVO userInfo = sysUserService.getUserInfo(userId);
        return Result.ok(userInfo);
    }

    @PostMapping("/logout")
    public Result logout() {
        SecurityContextHolder.clearContext();
        return Result.ok();
    }
}
