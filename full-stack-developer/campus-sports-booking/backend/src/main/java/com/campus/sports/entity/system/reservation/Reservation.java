package com.campus.sports.entity.system.reservation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.sports.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_reservation")
public class Reservation extends BaseEntity {

    private Long userId;
    
    private Long timeSlotId;
    
    private Long venueId;
    
    /** 状态: 0已取消 1已预约 2已完成 3违约 */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
    
    private LocalDateTime cancelledAt;
}
