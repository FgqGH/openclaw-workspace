package com.campus.sports.controller;

import com.campus.sports.common.result.Result;
import com.campus.sports.dto.ReservationRequest;
import com.campus.sports.entity.system.reservation.Reservation;
import com.campus.sports.entity.system.user.SystemUser;
import com.campus.sports.mapper.SystemUserMapper;
import com.campus.sports.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "预约管理")
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final SystemUserMapper userMapper;

    public ReservationController(ReservationService reservationService,
                                 SystemUserMapper userMapper) {
        this.reservationService = reservationService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "创建预约")
    @PostMapping
    public Result<Reservation> create(@RequestBody ReservationRequest request) {
        Long userId = getCurrentUserId();
        return reservationService.create(userId, request.getTimeSlotId());
    }

    @Operation(summary = "取消我的预约")
    @DeleteMapping("/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return reservationService.cancel(id, userId);
    }

    @Operation(summary = "获取我的预约列表")
    @GetMapping("/my")
    public Result<List<Reservation>> getMyReservations() {
        Long userId = getCurrentUserId();
        return reservationService.getMyReservations(userId);
    }

    @Operation(summary = "获取所有预约（管理员）")
    @GetMapping("/all")
    public Result<List<Reservation>> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(summary = "管理员取消预约")
    @DeleteMapping("/admin/{id}")
    public Result<Void> adminCancel(@PathVariable Long id) {
        return reservationService.adminCancel(id);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SystemUser user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUsername, username)
        );
        return user.getId();
    }
}
