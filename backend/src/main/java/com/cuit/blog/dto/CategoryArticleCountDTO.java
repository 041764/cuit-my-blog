package com.cuit.blog.dto;

import lombok.Data;

// 分类对应的文章数量统计 DTO
@Data
public class CategoryArticleCountDTO {

    // 分类ID
    private Long categoryId;

    // 文章数量
    private Long articleCount;
}
