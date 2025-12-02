package com.cuit.blog.service;

import com.cuit.blog.dto.request.CommentCreateRequest;
import com.cuit.blog.dto.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 评论服务测试类
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("评论服务测试")
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("创建顶级评论 - 成功")
    void testCreateComment_TopLevel() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(1L);
        request.setContent("这是一条服务测试评论");

        CommentResponse response = commentService.createComment(2L, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("这是一条服务测试评论", response.getContent());
    }

    @Test
    @DisplayName("创建回复评论 - 成功")
    void testCreateComment_Reply() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(1L);
        request.setParentId(1L);
        request.setReplyToUserId(3L);
        request.setContent("这是一条回复评论");

        CommentResponse response = commentService.createComment(2L, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("这是一条回复评论", response.getContent());
        assertNotNull(response.getReplyToUser());
    }

    @Test
    @DisplayName("获取文章评论 - 成功")
    void testGetArticleComments() {
        List<CommentResponse> comments = commentService.getArticleComments(1L);

        assertNotNull(comments);
        assertTrue(comments.size() > 0);
    }

    @Test
    @DisplayName("获取文章评论 - 文章不存在抛出异常")
    void testGetArticleComments_ArticleNotFound() {
        // 实际业务逻辑中，获取不存在文章的评论会抛出异常
        assertThrows(RuntimeException.class, () -> {
            commentService.getArticleComments(9999L);
        });
    }

    @Test
    @DisplayName("删除评论 - 作者删除成功")
    void testDeleteComment_ByAuthor() {
        assertDoesNotThrow(() -> {
            commentService.deleteComment(3L, 1L, false);
        });
    }

    @Test
    @DisplayName("删除评论 - 管理员删除成功")
    void testDeleteComment_ByAdmin() {
        assertDoesNotThrow(() -> {
            commentService.deleteComment(1L, 1L, true);
        });
    }

    @Test
    @DisplayName("删除评论 - 文章作者也可以删除")
    void testDeleteComment_ByArticleAuthor() {
        // 用户2是文章1的作者，应该可以删除该文章上的评论
        assertDoesNotThrow(() -> {
            commentService.deleteComment(2L, 1L, false);
        });
    }
}
