package com.campus.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.sports.common.exception.BusinessException;
import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.timeslot.TimeSlot;
import com.campus.sports.mapper.TimeSlotMapper;
import com.campus.sports.service.TimeSlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotMapper timeSlotMapper;

    public TimeSlotServiceImpl(TimeSlotMapper timeSlotMapper) {
        this.timeSlotMapper = timeSlotMapper;
    }

    @Override
    public Result<List<TimeSlot>> getByVenueAndDate(Long venueId, LocalDate date) {
        List<TimeSlot> list = timeSlotMapper.selectList(
            new LambdaQueryWrapper<TimeSlot>()
                .eq(TimeSlot::getVenueId, venueId)
                .eq(TimeSlot::getSlotDate, date)
                .orderByAsc(TimeSlot::getStartTime)
        );
        return Result.success(list);
    }

    @Override
    public Result<List<TimeSlot>> getAvailableSlots(Long venueId, LocalDate date) {
        List<TimeSlot> list = timeSlotMapper.selectList(
            new LambdaQueryWrapper<TimeSlot>()
                .eq(TimeSlot::getVenueId, venueId)
                .eq(TimeSlot::getSlotDate, date)
                .eq(TimeSlot::getStatus, 1) // 可预约
                .orderByAsc(TimeSlot::getStartTime)
        );
        return Result.success(list);
    }

    @Override
    public Result<Void> create(TimeSlot timeSlot) {
        // 检查是否已存在同时段
        TimeSlot existing = timeSlotMapper.selectOne(
            new LambdaQueryWrapper<TimeSlot>()
                .eq(TimeSlot::getVenueId, timeSlot.getVenueId())
                .eq(TimeSlot::getSlotDate, timeSlot.getSlotDate())
                .eq(TimeSlot::getStartTime, timeSlot.getStartTime())
                .eq(TimeSlot::getEndTime, timeSlot.getEndTime())
        );
        if (existing != null) {
            return Result.error("该时段已存在");
        }
        
        timeSlotMapper.insert(timeSlot);
        log.info("创建时段: 场地{} 日期{} {}~{}", 
            timeSlot.getVenueId(), timeSlot.getSlotDate(), 
            timeSlot.getStartTime(), timeSlot.getEndTime());
        return Result.success("创建成功", null);
    }

    @Override
    @Transactional
    public Result<Void> batchCreate(List<TimeSlot> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty()) {
            return Result.error("时段列表不能为空");
        }
        for (TimeSlot slot : timeSlots) {
            timeSlotMapper.insert(slot);
        }
        log.info("批量创建时段: {} 个", timeSlots.size());
        return Result.success("批量创建成功", null);
    }

    @Override
    public Result<Void> update(TimeSlot timeSlot) {
        if (timeSlot.getId() == null) {
            return Result.error("ID不能为空");
        }
        TimeSlot existing = timeSlotMapper.selectById(timeSlot.getId());
        if (existing == null) {
            return Result.error("时段不存在");
        }
        timeSlotMapper.updateById(timeSlot);
        log.info("更新时段: {}", timeSlot.getId());
        return Result.success("更新成功", null);
    }

    @Override
    public Result<Void> delete(Long id) {
        timeSlotMapper.deleteById(id);
        log.info("删除时段: {}", id);
        return Result.success("删除成功", null);
    }
}
