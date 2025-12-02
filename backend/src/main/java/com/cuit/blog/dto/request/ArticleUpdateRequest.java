package com.cuit.blog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

// 更新文章请求 DTO
@Data
public class ArticleUpdateRequest {

    // 文章标题
    @Size(max = 50, message = "文章标题不能超过50个字符")
    private String title;

    // 文章内容(Markdown)
    private String content;

    // 分类ID
    private Long categoryId;

    // 封面图URL
    private String coverImage;

    // 文章摘要
    @Size(max = 500, message = "文章摘要不能超过500个字符")
    private String summary;
}
