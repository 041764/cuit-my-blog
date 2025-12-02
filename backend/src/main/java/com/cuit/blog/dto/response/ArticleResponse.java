package com.cuit.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

// 文章详情响应 DTO
@Data
public class ArticleResponse {

    // 文章ID
    private Long id;

    // 文章标题
    private String title;

    // 文章内容(Markdown)
    private String content;

    // 封面图URL
    private String coverImage;

    // 文章摘要
    private String summary;

    // 分类信息
    private CategoryResponse category;

    // 作者信息
    private AuthorInfo author;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;

    // 作者简要信息
    @Data
    public static class AuthorInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
    }
}
