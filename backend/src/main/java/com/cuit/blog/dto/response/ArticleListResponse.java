package com.cuit.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

// 文章列表项响应 DTO（不包含完整内容）
@Data
public class ArticleListResponse {

    // 文章ID
    private Long id;

    // 文章标题
    private String title;

    // 封面图URL
    private String coverImage;

    // 文章摘要
    private String summary;

    // 分类信息
    private CategoryResponse category;

    // 作者信息
    private ArticleResponse.AuthorInfo author;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
