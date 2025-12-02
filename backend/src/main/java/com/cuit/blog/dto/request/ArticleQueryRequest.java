package com.cuit.blog.dto.request;

import lombok.Data;

// 文章查询请求 DTO
@Data
public class ArticleQueryRequest {

    // 当前页码，默认第1页
    private Integer pageNum = 1;

    // 每页数量，默认10条
    private Integer pageSize = 10;

    // 分类ID（可选）
    private Long categoryId;

    // 用户ID（可选）
    private Long userId;

    // 搜索关键词（可选，搜索标题）
    private String keyword;
}
