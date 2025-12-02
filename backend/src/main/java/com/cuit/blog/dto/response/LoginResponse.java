package com.cuit.blog.dto.response;

import lombok.Data;

// 登录响应DTO
@Data
public class LoginResponse {

    // 用户信息
    private UserResponse user;

    // 登录成功后颁发的访问令牌
    private String token;

    // 令牌类型
    private String tokenType = "Bearer";

    // 令牌有效期（毫秒）
    private Long expiresIn;

    // 提示信息
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(UserResponse user, String token, Long expiresIn, String message) {
        this.user = user;
        this.token = token;
        this.expiresIn = expiresIn;
        this.message = message;
    }
}
