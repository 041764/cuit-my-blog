package com.cuit.blog.service;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.dto.request.ArticleCreateRequest;
import com.cuit.blog.dto.request.ArticleQueryRequest;
import com.cuit.blog.dto.request.ArticleUpdateRequest;
import com.cuit.blog.dto.response.ArticleListResponse;
import com.cuit.blog.dto.response.ArticleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// 文章服务测试类
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("文章服务测试")
public class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("创建文章 - 成功")
    void testCreateArticle_Success() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("服务测试文章");
        request.setContent("# 服务测试\n\n这是测试内容");
        request.setCategoryId(1L);
        request.setSummary("服务测试文章摘要");

        ArticleResponse response = articleService.createArticle(2L, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("服务测试文章", response.getTitle());
        assertEquals("服务测试文章摘要", response.getSummary());
    }

    @Test
    @DisplayName("创建文章 - 最少字段")
    void testCreateArticle_MinimalFields() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("无标签文章");
        request.setContent("文章内容");
        request.setCategoryId(1L);

        ArticleResponse response = articleService.createArticle(2L, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("无标签文章", response.getTitle());
    }

    @Test
    @DisplayName("获取文章详情 - 成功")
    void testGetArticleById_Success() {
        ArticleResponse response = articleService.getArticleById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("第一篇测试文章", response.getTitle());
    }

    @Test
    @DisplayName("获取文章详情 - 不存在")
    void testGetArticleById_NotFound() {
        assertThrows(RuntimeException.class, () -> {
            articleService.getArticleById(9999L);
        });
    }

    @Test
    @DisplayName("获取文章列表 - 默认分页")
    void testGetArticleList_Default() {
        ArticleQueryRequest request = new ArticleQueryRequest();

        PageResult<ArticleListResponse> result = articleService.getArticleList(request);

        assertNotNull(result);
        assertNotNull(result.getList());
        assertTrue(result.getTotal() > 0);
    }

    @Test
    @DisplayName("获取文章列表 - 按关键词搜索")
    void testGetArticleList_SearchByKeyword() {
        ArticleQueryRequest request = new ArticleQueryRequest();
        request.setKeyword("测试");

        PageResult<ArticleListResponse> result = articleService.getArticleList(request);

        assertNotNull(result);
        assertNotNull(result.getList());
    }

    @Test
    @DisplayName("获取文章列表 - 摘要关键词搜索")
    void testGetArticleList_SearchBySummaryKeyword() {
        ArticleQueryRequest request = new ArticleQueryRequest();
        request.setKeyword("全文搜索");

        PageResult<ArticleListResponse> result = articleService.getArticleList(request);

        assertNotNull(result);
        assertNotNull(result.getList());
        assertFalse(result.getList().isEmpty());
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void testUpdateArticle_Success() {
        ArticleUpdateRequest request = new ArticleUpdateRequest();
        request.setTitle("更新后的文章标题");
        request.setContent("更新后的内容");

        ArticleResponse response = articleService.updateArticle(1L, 2L, request);

        assertNotNull(response);
        assertEquals("更新后的文章标题", response.getTitle());
    }

    @Test
    @DisplayName("更新文章 - 非作者更新失败")
    void testUpdateArticle_NotAuthor() {
        ArticleUpdateRequest request = new ArticleUpdateRequest();
        request.setTitle("更新后的文章标题");

        assertThrows(RuntimeException.class, () -> {
            articleService.updateArticle(1L, 3L, request); // 用户3不是文章1的作者
        });
    }

    @Test
    @DisplayName("删除文章 - 作者删除成功")
    void testDeleteArticle_ByAuthor() {
        assertDoesNotThrow(() -> {
            articleService.deleteArticle(1L, 2L, false);
        });
    }

    @Test
    @DisplayName("删除文章 - 管理员删除成功")
    void testDeleteArticle_ByAdmin() {
        assertDoesNotThrow(() -> {
            articleService.deleteArticle(1L, 1L, true);
        });
    }

    @Test
    @DisplayName("删除文章 - 非作者非管理员删除失败")
    void testDeleteArticle_NotAuthorNotAdmin() {
        assertThrows(RuntimeException.class, () -> {
            articleService.deleteArticle(1L, 3L, false);
        });
    }

    @Test
    @DisplayName("按分类获取文章")
    void testGetArticlesByCategory() {
        PageResult<ArticleListResponse> result = articleService.getArticlesByCategory(1L, 1, 10);

        assertNotNull(result);
        assertNotNull(result.getList());
    }

}
