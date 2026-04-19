package com.campus.sports.entity.system.venue;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.sports.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_venue")
public class Venue extends BaseEntity {

    private Long categoryId;
    
    private String name;
    
    private String location;
    
    private Integer capacity;
    
    private String description;
    
    private String imageUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
}
