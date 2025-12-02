package com.cuit.blog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

// 更新用户资料请求DTO
@Data
public class UserProfileUpdateRequest {

    // 昵称
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    // 个人简介
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;
}
