package com.campus.sports.controller;

import com.campus.sports.common.result.Result;
import com.campus.sports.dto.LoginRequest;
import com.campus.sports.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return authService.logout();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<?> getCurrentUser() {
        return authService.getCurrentUser();
    }
}
