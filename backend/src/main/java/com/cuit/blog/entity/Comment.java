package com.cuit.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// 评论实体类
@Data
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 文章ID
    private Long articleId;

    // 评论者ID
    private Long userId;

    // 父评论ID（NULL表示顶级评论）
    private Long parentId;

    // 被回复用户ID
    private Long replyToUserId;

    // 评论内容
    private String content;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
