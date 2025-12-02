package com.cuit.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 创建文章请求 DTO
@Data
public class ArticleCreateRequest {

    // 文章标题
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 50, message = "文章标题不能超过50个字符")
    private String title;

    // 文章内容(Markdown)
    @NotBlank(message = "文章内容不能为空")
    private String content;

    // 分类ID
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    // 封面图URL
    private String coverImage;

    // 文章摘要
    @Size(max = 500, message = "文章摘要不能超过500个字符")
    private String summary;
}
