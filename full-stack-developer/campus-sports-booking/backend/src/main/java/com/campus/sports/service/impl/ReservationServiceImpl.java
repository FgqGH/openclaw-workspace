package com.campus.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.sports.common.exception.BusinessException;
import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.reservation.Reservation;
import com.campus.sports.entity.system.timeslot.TimeSlot;
import com.campus.sports.entity.system.user.SystemUser;
import com.campus.sports.mapper.ReservationMapper;
import com.campus.sports.mapper.SystemUserMapper;
import com.campus.sports.mapper.TimeSlotMapper;
import com.campus.sports.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class ReservationServiceImpl implements ReservationService {

    private static final int MAX_RESERVATIONS_PER_DAY = 3;
    private static final int MIN_CANCEL_BEFORE_MINUTES = 30;

    private final ReservationMapper reservationMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final SystemUserMapper userMapper;

    public ReservationServiceImpl(ReservationMapper reservationMapper,
                                  TimeSlotMapper timeSlotMapper,
                                  SystemUserMapper userMapper) {
        this.reservationMapper = reservationMapper;
        this.timeSlotMapper = timeSlotMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Result<Reservation> create(Long userId, Long timeSlotId) {
        // 1. 检查时段是否存在且可预约
        TimeSlot timeSlot = timeSlotMapper.selectById(timeSlotId);
        if (timeSlot == null) {
            return Result.error("时段不存在");
        }
        if (timeSlot.getStatus() != 1) {
            return Result.error("该时段不可预约");
        }
        if (timeSlot.getBookedCount() >= timeSlot.getMaxBookings()) {
            return Result.error("该时段已约满");
        }

        // 2. 检查用户是否已有该时段的预约
        Reservation existing = reservationMapper.selectOne(
            new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .eq(Reservation::getTimeSlotId, timeSlotId)
                .eq(Reservation::getStatus, 1)
        );
        if (existing != null) {
            return Result.error("您已预约该时段");
        }

        // 3. 检查用户当日预约次数上限
        LocalDateTime todayStart = timeSlot.getSlotDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        List<Reservation> todayReservations = reservationMapper.selectList(
            new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .eq(Reservation::getStatus, 1)
                .ge(Reservation::getCreatedAt, todayStart)
                .lt(Reservation::getCreatedAt, todayEnd)
        );
        if (todayReservations.size() >= MAX_RESERVATIONS_PER_DAY) {
            return Result.error("每人每天最多预约" + MAX_RESERVATIONS_PER_DAY + "个时段");
        }

        // 4. 创建预约
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setTimeSlotId(timeSlotId);
        reservation.setVenueId(timeSlot.getVenueId());
        reservation.setStatus(1); // 已预约
        
        reservationMapper.insert(reservation);

        // 5. 更新时段已预约数
        timeSlotMapper.update(null,
            new LambdaUpdateWrapper<TimeSlot>()
                .eq(TimeSlot::getId, timeSlotId)
                .setSql("booked_count = booked_count + 1")
                .apply("booked_count >= max_bookings", "status = 2")
        );

        log.info("用户 {} 预约场地 {} 时段 {}", userId, timeSlot.getVenueId(), timeSlotId);
        return Result.success("预约成功", reservation);
    }

    @Override
    @Transactional
    public Result<Void> cancel(Long reservationId, Long userId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            return Result.error("无权取消该预约");
        }
        if (reservation.getStatus() != 1) {
            return Result.error("该预约已无法取消");
        }

        // 检查取消期限（开始前30分钟）
        TimeSlot timeSlot = timeSlotMapper.selectById(reservation.getTimeSlotId());
        LocalDateTime slotStart = timeSlot.getSlotDate().atTime(timeSlot.getStartTime());
        long minutesUntilStart = ChronoUnit.MINUTES.between(LocalDateTime.now(), slotStart);
        
        if (minutesUntilStart < MIN_CANCEL_BEFORE_MINUTES) {
            return Result.error("距离场地开始不足" + MIN_CANCEL_BEFORE_MINUTES + "分钟，无法取消");
        }

        // 取消预约
        reservationMapper.update(null,
            new LambdaUpdateWrapper<Reservation>()
                .eq(Reservation::getId, reservationId)
                .set(Reservation::getStatus, 0)
                .set(Reservation::getCancelledAt, LocalDateTime.now())
        );

        // 减少时段已预约数
        timeSlotMapper.update(null,
            new LambdaUpdateWrapper<TimeSlot>()
                .eq(TimeSlot::getId, reservation.getTimeSlotId())
                .setSql("booked_count = GREATEST(booked_count - 1, 0)")
                .set(TimeSlot::getStatus, 1)
        );

        log.info("用户 {} 取消预约 {}", userId, reservationId);
        return Result.success("取消成功", null);
    }

    @Override
    public Result<List<Reservation>> getMyReservations(Long userId) {
        List<Reservation> list = reservationMapper.selectList(
            new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .orderByDesc(Reservation::getCreatedAt)
        );
        return Result.success(list);
    }

    @Override
    public Result<List<Reservation>> getAllReservations() {
        List<Reservation> list = reservationMapper.selectList(
            new LambdaQueryWrapper<Reservation>()
                .orderByDesc(Reservation::getCreatedAt)
        );
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<Void> adminCancel(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        if (reservation.getStatus() != 1) {
            return Result.error("该预约已无法取消");
        }

        reservationMapper.update(null,
            new LambdaUpdateWrapper<Reservation>()
                .eq(Reservation::getId, reservationId)
                .set(Reservation::getStatus, 0)
                .set(Reservation::getCancelledAt, LocalDateTime.now())
        );

        // 减少时段已预约数
        timeSlotMapper.update(null,
            new LambdaUpdateWrapper<TimeSlot>()
                .eq(TimeSlot::getId, reservation.getTimeSlotId())
                .setSql("booked_count = GREATEST(booked_count - 1, 0)")
                .set(TimeSlot::getStatus, 1)
        );

        log.info("管理员取消预约 {}", reservationId);
        return Result.success("取消成功", null);
    }
}
