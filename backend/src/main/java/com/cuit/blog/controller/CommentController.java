package com.cuit.blog.controller;

import com.cuit.blog.common.result.Result;
import com.cuit.blog.common.utils.SecurityUtils;
import com.cuit.blog.dto.request.CommentCreateRequest;
import com.cuit.blog.dto.response.CommentResponse;
import com.cuit.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

// 评论控制器
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发表评论
     * POST /api/comments
     *
     * @param userId  用户ID（从请求头获取）
     * @param request 评论请求
     * @return 评论响应
     */
    @PostMapping
    public Result<CommentResponse> createComment(
            @Valid @RequestBody CommentCreateRequest request) {
        Long userId = SecurityUtils.getRequiredUserId();
        CommentResponse response = commentService.createComment(userId, request);
        return Result.success(response);
    }

    /**
     * 获取文章评论（树形结构）
     * GET /api/comments/article/{articleId}
     *
     * @param articleId 文章ID
     * @return 评论列表（树形）
     */
    @GetMapping("/article/{articleId}")
    public Result<List<CommentResponse>> getArticleComments(@PathVariable Long articleId) {
        List<CommentResponse> comments = commentService.getArticleComments(articleId);
        return Result.success(comments);
    }

    /**
     * 删除评论
     * DELETE /api/comments/{id}
     *
     * @param userId 用户ID（从请求头获取）
     * @param role   用户角色（从请求头获取）
     * @param id     评论ID
     * @return 成功结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(
            @PathVariable Long id) {
        Long userId = SecurityUtils.getRequiredUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        commentService.deleteComment(userId, id, isAdmin);
        return Result.success();
    }
}
