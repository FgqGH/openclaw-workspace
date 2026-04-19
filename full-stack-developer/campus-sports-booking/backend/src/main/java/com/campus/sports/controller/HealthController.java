package com.campus.sports.controller;

import com.campus.sports.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * 健康检查接口
 * 注意：这个接口只返回静态信息，不涉及任何业务逻辑
 * 不写入数据库、不操作任何资源
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * GET /api/health
     * 无状态健康检查端点，供监控系统调用
     * 不涉及任何业务逻辑，不写入数据库
     */
    @GetMapping("/health")
    public Result<HealthInfo> healthCheck() {
        return Result.success(new HealthInfo("ok", Instant.now().toString()));
    }

    public record HealthInfo(String status, String timestamp) {}
}
