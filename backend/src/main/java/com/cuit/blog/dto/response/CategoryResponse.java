package com.cuit.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

// 分类响应 DTO
@Data
public class CategoryResponse {

    // 分类ID
    private Long id;

    // 分类名称
    private String name;

    // 分类描述
    private String description;

    // 创建时间
    private LocalDateTime createdAt;

    // 该分类下的文章数量
    private Long articleCount;
}
