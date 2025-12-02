package com.cuit.blog.service;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.dto.request.ArticleCreateRequest;
import com.cuit.blog.dto.request.ArticleQueryRequest;
import com.cuit.blog.dto.request.ArticleUpdateRequest;
import com.cuit.blog.dto.response.ArticleListResponse;
import com.cuit.blog.dto.response.ArticleResponse;

// 文章服务接口
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param userId  用户ID
     * @param request 创建文章请求
     * @return 文章详情
     */
    ArticleResponse createArticle(Long userId, ArticleCreateRequest request);

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleResponse getArticleById(Long id);

    /**
     * 更新文章
     *
     * @param id      文章ID
     * @param userId  用户ID
     * @param request 更新文章请求
     * @return 文章详情
     */
    ArticleResponse updateArticle(Long id, Long userId, ArticleUpdateRequest request);

    /**
     * 删除文章
     *
     * @param id     文章ID
     * @param userId 用户ID
     * @param isAdmin 是否为管理员
     */
    void deleteArticle(Long id, Long userId, boolean isAdmin);

    /**
     * 分页查询文章列表
     *
     * @param request 查询请求
     * @return 分页文章列表
     */
    PageResult<ArticleListResponse> getArticleList(ArticleQueryRequest request);

    /**
     * 按分类查询文章列表
     *
     * @param categoryId 分类ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 分页文章列表
     */
    PageResult<ArticleListResponse> getArticlesByCategory(Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的文章列表
     *
     * @param userId   用户ID
     * @param keyword  搜索关键词（可选）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页文章列表
     */
    PageResult<ArticleListResponse> getArticlesByUser(Long userId, String keyword, Integer pageNum, Integer pageSize);
}
