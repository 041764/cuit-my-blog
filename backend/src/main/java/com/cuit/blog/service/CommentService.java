package com.cuit.blog.service;

import com.cuit.blog.dto.request.CommentCreateRequest;
import com.cuit.blog.dto.response.CommentResponse;

import java.util.List;

// 评论服务接口
public interface CommentService {

    /**
     * 创建评论
     *
     * @param userId  评论者ID
     * @param request 评论请求
     * @return 评论响应
     */
    CommentResponse createComment(Long userId, CommentCreateRequest request);

    /**
     * 获取文章评论（树形结构）
     *
     * @param articleId 文章ID
     * @return 评论列表（树形）
     */
    List<CommentResponse> getArticleComments(Long articleId);

    /**
     * 删除评论
     *
     * @param userId    操作者ID
     * @param commentId 评论ID
     * @param isAdmin   是否为管理员
     */
    void deleteComment(Long userId, Long commentId, boolean isAdmin);
}
