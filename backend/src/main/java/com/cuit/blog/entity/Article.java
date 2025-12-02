package com.cuit.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// 文章实体类
@Data
@TableName("article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 作者ID
    private Long userId;

    // 分类ID
    private Long categoryId;

    // 文章标题
    private String title;

    // 文章内容(Markdown)
    private String content;

    // 封面图URL
    private String coverImage;

    // 文章摘要
    private String summary;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
