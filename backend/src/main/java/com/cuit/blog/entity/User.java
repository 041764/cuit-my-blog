package com.cuit.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// 用户实体类
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户名（唯一）
    private String username;

    // 密码
    private String password;

    // 邮箱（唯一）
    private String email;

    // 昵称
    private String nickname;

    // 头像URL
    private String avatar;

    // 个人简介
    private String bio;

    // 角色(ADMIN/USER)
    private String role;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
