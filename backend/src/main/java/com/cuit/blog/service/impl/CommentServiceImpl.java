package com.cuit.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.dto.request.CommentCreateRequest;
import com.cuit.blog.dto.response.CommentResponse;
import com.cuit.blog.entity.Article;
import com.cuit.blog.entity.Comment;
import com.cuit.blog.entity.User;
import com.cuit.blog.mapper.ArticleMapper;
import com.cuit.blog.mapper.CommentMapper;
import com.cuit.blog.mapper.UserMapper;
import com.cuit.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

// 评论服务实现类
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, CommentCreateRequest request) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(request.getArticleId());
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 如果是回复评论，检查父评论是否存在
        Comment parentComment = null;
        if (request.getParentId() != null) {
            parentComment = commentMapper.selectById(request.getParentId());
            if (parentComment == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "父评论不存在");
            }
            // 确保父评论属于同一篇文章
            if (!parentComment.getArticleId().equals(request.getArticleId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "父评论不属于该文章");
            }
        }

        // 如果指定了被回复用户，检查用户是否存在
        User replyToUser = null;
        if (request.getReplyToUserId() != null) {
            replyToUser = userMapper.selectById(request.getReplyToUserId());
            if (replyToUser == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "被回复用户不存在");
            }
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setReplyToUserId(request.getReplyToUserId());
        comment.setContent(request.getContent());

        commentMapper.insert(comment);

        // 构建响应
        return buildCommentResponse(comment, user, replyToUser);
    }

    @Override
    public List<CommentResponse> getArticleComments(Long articleId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        // 获取文章的所有评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId)
                .orderByAsc(Comment::getCreatedAt);
        List<Comment> comments = commentMapper.selectList(queryWrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有相关用户ID
        Set<Long> userIds = new HashSet<>();
        for (Comment comment : comments) {
            userIds.add(comment.getUserId());
            if (comment.getReplyToUserId() != null) {
                userIds.add(comment.getReplyToUserId());
            }
        }

        // 批量查询用户信息
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        // 构建树形结构
        return buildCommentTree(comments, userMap);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId, boolean isAdmin) {
        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }

        // 获取文章信息
        Article article = articleMapper.selectById(comment.getArticleId());

        // 权限检查：评论作者、文章作者、管理员可以删除
        boolean isCommentAuthor = comment.getUserId().equals(userId);
        boolean isArticleAuthor = article != null && article.getUserId().equals(userId);

        if (!isCommentAuthor && !isArticleAuthor && !isAdmin) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此评论");
        }

        // 删除评论及其所有子评论
        deleteCommentAndChildren(commentId);
    }

    // 递归删除评论及其子评论
    private void deleteCommentAndChildren(Long commentId) {
        // 查找所有子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, commentId);
        List<Comment> children = commentMapper.selectList(queryWrapper);

        // 递归删除子评论
        for (Comment child : children) {
            deleteCommentAndChildren(child.getId());
        }

        // 删除当前评论
        commentMapper.deleteById(commentId);
    }

    // 构建评论响应
    private CommentResponse buildCommentResponse(Comment comment, User user, User replyToUser) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setArticleId(comment.getArticleId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        // 设置评论者信息
        if (user != null) {
            CommentResponse.CommentUserInfo userInfo = new CommentResponse.CommentUserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            response.setUser(userInfo);
        }

        // 设置被回复用户信息
        if (replyToUser != null) {
            CommentResponse.CommentUserInfo replyToUserInfo = new CommentResponse.CommentUserInfo();
            replyToUserInfo.setId(replyToUser.getId());
            replyToUserInfo.setUsername(replyToUser.getUsername());
            replyToUserInfo.setNickname(replyToUser.getNickname());
            replyToUserInfo.setAvatar(replyToUser.getAvatar());
            response.setReplyToUser(replyToUserInfo);
        }

        response.setChildren(new ArrayList<>());
        return response;
    }

    // 构建评论树形结构
    private List<CommentResponse> buildCommentTree(List<Comment> comments, Map<Long, User> userMap) {
        // 将评论转换为响应对象
        Map<Long, CommentResponse> responseMap = new HashMap<>();
        for (Comment comment : comments) {
            User user = userMap.get(comment.getUserId());
            User replyToUser = comment.getReplyToUserId() != null ? userMap.get(comment.getReplyToUserId()) : null;
            CommentResponse response = buildCommentResponse(comment, user, replyToUser);
            responseMap.put(comment.getId(), response);
        }

        // 构建树形结构
        List<CommentResponse> rootComments = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse response = responseMap.get(comment.getId());
            if (comment.getParentId() == null) {
                // 顶级评论
                rootComments.add(response);
            } else {
                // 子评论，添加到父评论的children中
                CommentResponse parent = responseMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getChildren().add(response);
                }
            }
        }

        return rootComments;
    }
}
