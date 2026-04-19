package com.campus.sports.entity.system.role;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.sports.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_role")
public class SystemRole extends BaseEntity {

    private String roleCode;
    
    private String roleName;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
}
