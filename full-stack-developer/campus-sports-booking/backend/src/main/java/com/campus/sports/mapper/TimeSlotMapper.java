package com.campus.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.sports.entity.system.timeslot.TimeSlot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TimeSlotMapper extends BaseMapper<TimeSlot> {
}
