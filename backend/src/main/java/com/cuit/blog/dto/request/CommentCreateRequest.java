package com.cuit.blog.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 创建评论请求DTO
@Data
public class CommentCreateRequest {

    // 文章ID
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    // 父评论ID（NULL表示顶级评论）
    private Long parentId;

    // 被回复用户ID（回复楼中楼时使用）
    private Long replyToUserId;

    // 评论内容
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000字符之间")
    private String content;
}
