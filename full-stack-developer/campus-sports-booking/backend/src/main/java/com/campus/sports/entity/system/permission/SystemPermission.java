package com.campus.sports.entity.system.permission;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.sports.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_permission")
public class SystemPermission extends BaseEntity {

    private Long parentId;
    
    private String name;
    
    private String permissionCode;
    
    /** 类型: 1菜单 2按钮 3API */
    private Integer resourceType;
    
    private String path;
    
    private String icon;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
}
