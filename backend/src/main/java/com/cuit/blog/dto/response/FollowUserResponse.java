package com.cuit.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

// 关注用户响应DTO
@Data
public class FollowUserResponse {

    // 用户ID
    private Long id;

    // 用户名
    private String username;

    // 昵称
    private String nickname;

    // 头像URL
    private String avatar;

    // 个人简介
    private String bio;

    // 关注时间
    private LocalDateTime followedAt;
}
