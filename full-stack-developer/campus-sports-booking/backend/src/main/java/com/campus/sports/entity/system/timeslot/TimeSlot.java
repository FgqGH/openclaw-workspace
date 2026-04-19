package com.campus.sports.entity.system.timeslot;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.sports.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_time_slot")
public class TimeSlot extends BaseEntity {

    private Long venueId;
    
    private LocalDate slotDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer maxBookings;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer bookedCount;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
}
