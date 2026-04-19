package com.campus.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.sports.common.exception.BusinessException;
import com.campus.sports.common.result.Result;
import com.campus.sports.common.util.JwtUtil;
import com.campus.sports.entity.system.user.SystemUser;
import com.campus.sports.mapper.SystemUserMapper;
import com.campus.sports.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SystemUserMapper userMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          SystemUserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public Result<String> login(String username, String password) {
        try {
            // 先检查用户是否存在
            SystemUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, username)
            );
            
            if (user == null) {
                return Result.unauthorized("用户不存在");
            }
            
            if (user.getStatus() == 0) {
                return Result.unauthorized("账号已被禁用");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return Result.unauthorized("用户名或密码错误");
            }
            
            // 生成 JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtUtil.generateToken(userDetails);
            
            log.info("用户登录成功: {}", username);
            return Result.success("登录成功", token);
            
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage());
            return Result.unauthorized("登录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功", null);
    }

    @Override
    public Result<SystemUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.unauthorized("未登录");
        }
        
        String username = authentication.getName();
        SystemUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, username)
        );
        
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }
        
        // 脱敏
        user.setPassword(null);
        return Result.success(user);
    }
}
