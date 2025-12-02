package com.cuit.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// 分类实体类
@Data
@TableName("category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 分类名称
    private String name;

    // 分类描述
    private String description;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
