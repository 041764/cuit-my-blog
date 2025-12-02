package com.cuit.blog.controller;

import com.cuit.blog.dto.request.ArticleCreateRequest;
import com.cuit.blog.dto.request.ArticleUpdateRequest;
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

// 文章控制器测试类
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("文章模块测试")
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTokenProvider tokenProvider;

    @Test
    @DisplayName("发布文章 - 成功")
    void testCreateArticle_Success() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("新建测试文章");
        request.setContent("# 文章内容\n这是测试内容");
        request.setCategoryId(1L);
        request.setSummary("这是文章摘要");

        mockMvc.perform(post("/api/articles")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("新建测试文章"));
    }

    @Test
    @DisplayName("发布文章 - 标题为空")
    void testCreateArticle_TitleEmpty() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("");
        request.setContent("文章内容");
        request.setCategoryId(1L);

        mockMvc.perform(post("/api/articles")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("发布文章 - 内容为空")
    void testCreateArticle_ContentEmpty() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("测试标题");
        request.setContent("");
        request.setCategoryId(1L);

        mockMvc.perform(post("/api/articles")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("发布文章 - 分类ID为空")
    void testCreateArticle_CategoryIdNull() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("测试标题");
        request.setContent("文章内容");
        request.setCategoryId(null);

        mockMvc.perform(post("/api/articles")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取文章列表 - 默认分页")
    void testGetArticleList_DefaultPagination() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @DisplayName("获取文章列表 - 自定义分页")
    void testGetArticleList_CustomPagination() throws Exception {
        mockMvc.perform(get("/api/articles")
                        .param("pageNum", "1")
                        .param("pageSize", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(5));
    }

    @Test
    @DisplayName("获取文章列表 - 按关键词搜索")
    void testGetArticleList_SearchByKeyword() throws Exception {
        mockMvc.perform(get("/api/articles")
                        .param("keyword", "测试"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

        @Test
        @DisplayName("获取文章列表 - 摘要关键词搜索")
        void testGetArticleList_SearchBySummaryKeyword() throws Exception {
                mockMvc.perform(get("/api/articles")
                                                .param("keyword", "全文搜索"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.list").isArray());
        }

    @Test
    @DisplayName("获取文章详情 - 成功")
    void testGetArticleById_Success() throws Exception {
        mockMvc.perform(get("/api/articles/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("第一篇测试文章"));
    }

    @Test
    @DisplayName("获取文章详情 - 文章不存在")
    void testGetArticleById_NotFound() throws Exception {
        mockMvc.perform(get("/api/articles/9999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(2001));
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void testUpdateArticle_Success() throws Exception {
        ArticleUpdateRequest request = new ArticleUpdateRequest();
        request.setTitle("更新后的标题");
        request.setContent("更新后的内容");
        request.setCategoryId(2L);

        mockMvc.perform(put("/api/articles/1")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("更新后的标题"));
    }

    @Test
    @DisplayName("更新文章 - 非作者更新")
    void testUpdateArticle_NotAuthor() throws Exception {
        ArticleUpdateRequest request = new ArticleUpdateRequest();
        request.setTitle("更新后的标题");
        request.setContent("更新后的内容");

        mockMvc.perform(put("/api/articles/1")
                .header("Authorization", tokenProvider.bearerToken(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("删除文章 - 作者删除成功")
    void testDeleteArticle_ByAuthor() throws Exception {
        mockMvc.perform(delete("/api/articles/1")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除文章 - 管理员删除成功")
    void testDeleteArticle_ByAdmin() throws Exception {
        mockMvc.perform(delete("/api/articles/1")
                .header("Authorization", tokenProvider.bearerToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

        @Test
        @DisplayName("删除文章 - 管理员不能删除其他管理员文章")
        void testDeleteArticle_AdminCannotDeleteAnotherAdmin() throws Exception {
                mockMvc.perform(delete("/api/articles/4")
                                                .header("Authorization", tokenProvider.bearerToken(1L)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(403));
        }

        @Test
        @DisplayName("删除文章 - 管理员删除自己的文章")
        void testDeleteArticle_AdminDeletesOwnArticle() throws Exception {
                mockMvc.perform(delete("/api/articles/4")
                                                .header("Authorization", tokenProvider.bearerToken(4L)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

    @Test
    @DisplayName("删除文章 - 非作者删除失败")
    void testDeleteArticle_NotAuthor() throws Exception {
        mockMvc.perform(delete("/api/articles/1")
                .header("Authorization", tokenProvider.bearerToken(3L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("按分类查找文章")
    void testGetArticlesByCategory() throws Exception {
        mockMvc.perform(get("/api/articles/category/1")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

}
