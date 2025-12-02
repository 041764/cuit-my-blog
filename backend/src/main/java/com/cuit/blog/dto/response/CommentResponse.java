package com.cuit.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// 评论响应DTO（支持树形结构）
@Data
public class CommentResponse {

    // 评论ID
    private Long id;

    // 文章ID
    private Long articleId;

    // 评论内容
    private String content;

    // 评论者信息
    private CommentUserInfo user;

    // 被回复用户信息（楼中楼时有值）
    private CommentUserInfo replyToUser;

    // 创建时间
    private LocalDateTime createdAt;

    // 子评论列表（树形结构）
    private List<CommentResponse> children;

    // 评论用户简要信息
    @Data
    public static class CommentUserInfo {
        // 用户ID
        private Long id;

        // 用户名
        private String username;

        // 昵称
        private String nickname;

        // 头像URL
        private String avatar;
    }
}
