package com.cuit.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

// 用户信息响应DTO
@Data
public class UserResponse {

    // 用户ID
    private Long id;

    // 用户名
    private String username;

    // 邮箱
    private String email;

    // 昵称
    private String nickname;

    // 头像URL
    private String avatar;

    // 个人简介
    private String bio;

    // 角色
    private String role;

    // 创建时间
    private LocalDateTime createdAt;
}
