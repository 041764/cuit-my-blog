package com.cuit.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 创建分类请求 DTO
@Data
public class CategoryRequest {

    // 分类名称
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String name;

    // 分类描述
    @Size(max = 255, message = "分类描述不能超过255个字符")
    private String description;
}
