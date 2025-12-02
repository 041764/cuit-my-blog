package com.cuit.blog.controller;

import com.cuit.blog.dto.request.CategoryRequest;
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

// 分类控制器测试类
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("分类模块测试")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTokenProvider tokenProvider;

    @Test
    @DisplayName("获取所有分类列表")
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @DisplayName("创建分类 - 管理员成功")
    void testCreateCategory_AdminSuccess() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("新分类");
        request.setDescription("新分类描述");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", tokenProvider.bearerToken(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("新分类"));
    }

    @Test
    @DisplayName("创建分类 - 普通用户失败")
    void testCreateCategory_UserFailed() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("新分类");
        request.setDescription("新分类描述");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(403))
            .andExpect(jsonPath("$.message").value("没有相关权限"));
    }

    @Test
    @DisplayName("创建分类 - 名称为空")
    void testCreateCategory_NameEmpty() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("");
        request.setDescription("描述");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", tokenProvider.bearerToken(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取分类详情 - 成功")
    void testGetCategoryById_Success() throws Exception {
        mockMvc.perform(get("/api/categories/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Java"));
    }

    @Test
    @DisplayName("获取分类详情 - 不存在")
    void testGetCategoryById_NotFound() throws Exception {
        mockMvc.perform(get("/api/categories/9999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(2002));
    }

    @Test
    @DisplayName("更新分类 - 管理员成功")
    void testUpdateCategory_AdminSuccess() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("更新后的分类名");
        request.setDescription("更新后的描述");

        mockMvc.perform(put("/api/categories/1")
                .header("Authorization", tokenProvider.bearerToken(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("更新后的分类名"));
    }

    @Test
    @DisplayName("更新分类 - 普通用户失败")
    void testUpdateCategory_UserFailed() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("更新后的分类名");

        mockMvc.perform(put("/api/categories/1")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(403))
            .andExpect(jsonPath("$.message").value("没有相关权限"));
    }

    @Test
    @DisplayName("删除分类 - 管理员成功")
    void testDeleteCategory_AdminSuccess() throws Exception {
        mockMvc.perform(delete("/api/categories/3")
                .header("Authorization", tokenProvider.bearerToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除分类 - 普通用户失败")
    void testDeleteCategory_UserFailed() throws Exception {
        mockMvc.perform(delete("/api/categories/1")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(403))
            .andExpect(jsonPath("$.message").value("没有相关权限"));
    }
}
