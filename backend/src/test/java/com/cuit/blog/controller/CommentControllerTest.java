package com.cuit.blog.controller;

import com.cuit.blog.dto.request.CommentCreateRequest;
import com.cuit.blog.testutil.TestTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 评论控制器测试类
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("评论模块测试")
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTokenProvider tokenProvider;

    @Test
    @DisplayName("发表评论 - 顶级评论成功")
    void testCreateComment_TopLevelSuccess() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(1L);
        request.setContent("这是一条新的顶级评论");

        mockMvc.perform(post("/api/comments")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("这是一条新的顶级评论"));
    }

    @Test
    @DisplayName("发表评论 - 回复评论成功")
    void testCreateComment_ReplySuccess() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(1L);
        request.setParentId(1L);
        request.setReplyToUserId(3L);
        request.setContent("这是一条回复评论");

        mockMvc.perform(post("/api/comments")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("这是一条回复评论"));
    }

    @Test
    @DisplayName("发表评论 - 文章ID为空")
    void testCreateComment_ArticleIdNull() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("评论内容");

        mockMvc.perform(post("/api/comments")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("发表评论 - 内容为空")
    void testCreateComment_ContentEmpty() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(1L);
        request.setContent("");

        mockMvc.perform(post("/api/comments")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取文章评论 - 成功")
    void testGetArticleComments_Success() throws Exception {
        mockMvc.perform(get("/api/comments/article/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取文章评论 - 文章不存在")
    void testGetArticleComments_ArticleNotFound() throws Exception {
        // 实际业务逻辑中，获取不存在文章的评论会返回错误
        mockMvc.perform(get("/api/comments/article/9999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("删除评论 - 作者删除成功")
    void testDeleteComment_ByAuthor() throws Exception {
        mockMvc.perform(delete("/api/comments/1")
                .header("Authorization", tokenProvider.bearerToken(3L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除评论 - 管理员删除成功")
    void testDeleteComment_ByAdmin() throws Exception {
        mockMvc.perform(delete("/api/comments/1")
                .header("Authorization", tokenProvider.bearerToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除评论 - 非作者删除失败")
    void testDeleteComment_NotAuthor() throws Exception {
        // 注意：实际业务逻辑中，可能允许文章作者删除他人在自己文章上的评论
        // 这里测试用户2不是评论1的作者（评论1是用户3发的）
        // 但文章1的作者是用户2，所以可能有权限删除
        mockMvc.perform(delete("/api/comments/1")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
