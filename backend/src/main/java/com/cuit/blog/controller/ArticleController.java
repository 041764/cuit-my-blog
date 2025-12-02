package com.cuit.blog.controller;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.common.result.Result;
import com.cuit.blog.common.utils.SecurityUtils;
import com.cuit.blog.dto.request.ArticleCreateRequest;
import com.cuit.blog.dto.request.ArticleQueryRequest;
import com.cuit.blog.dto.request.ArticleUpdateRequest;
import com.cuit.blog.dto.response.ArticleListResponse;
import com.cuit.blog.dto.response.ArticleResponse;
import com.cuit.blog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 文章控制器
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 发布文章
     *
     * @param userId  当前用户ID（由请求头传递）
     * @param request 创建文章请求
     * @return 文章详情
     */
    @PostMapping
    public Result<ArticleResponse> createArticle(
            @Valid @RequestBody ArticleCreateRequest request) {
        Long userId = SecurityUtils.getRequiredUserId();
        ArticleResponse response = articleService.createArticle(userId, request);
        return Result.success(response);
    }

    /**
     * 获取文章列表（分页）
     *
     * @param request 查询请求
     * @return 分页文章列表
     */
    @GetMapping
    public Result<PageResult<ArticleListResponse>> getArticleList(ArticleQueryRequest request) {
        PageResult<ArticleListResponse> result = articleService.getArticleList(request);
        return Result.success(result);
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleResponse> getArticleById(@PathVariable Long id) {
        ArticleResponse response = articleService.getArticleById(id);
        return Result.success(response);
    }

    /**
     * 更新文章
     *
     * @param id      文章ID
     * @param userId  当前用户ID
     * @param request 更新文章请求
     * @return 文章详情
     */
    @PutMapping("/{id}")
    public Result<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleUpdateRequest request) {
        Long userId = SecurityUtils.getRequiredUserId();
        ArticleResponse response = articleService.updateArticle(id, userId, request);
        return Result.success(response);
    }

    /**
     * 删除文章
     *
     * @param id      文章ID
     * @param userId  当前用户ID
     * @param isAdmin 是否为管理员
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(
            @PathVariable Long id) {
        Long userId = SecurityUtils.getRequiredUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        articleService.deleteArticle(id, userId, isAdmin);
        return Result.success();
    }

    /**
     * 按分类查找文章
     *
     * @param categoryId 分类ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 分页文章列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageResult<ArticleListResponse>> getArticlesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ArticleListResponse> result = articleService.getArticlesByCategory(categoryId, pageNum, pageSize);
        return Result.success(result);
    }

}
