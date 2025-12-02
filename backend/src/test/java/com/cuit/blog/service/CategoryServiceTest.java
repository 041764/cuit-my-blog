package com.cuit.blog.service;

import com.cuit.blog.dto.request.CategoryRequest;
import com.cuit.blog.dto.response.CategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 分类服务测试类
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("分类服务测试")
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    @DisplayName("获取所有分类")
    void testGetAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertEquals(3, categories.size());
    }

    @Test
    @DisplayName("创建分类 - 成功")
    void testCreateCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("新分类");
        request.setDescription("新分类描述");

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("新分类", response.getName());
        assertEquals("新分类描述", response.getDescription());
    }

    @Test
    @DisplayName("创建分类 - 名称重复")
    void testCreateCategory_DuplicateName() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Java"); // 测试数据中已存在

        assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(request);
        });
    }

    @Test
    @DisplayName("获取分类详情 - 成功")
    void testGetCategoryById_Success() {
        CategoryResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java", response.getName());
    }

    @Test
    @DisplayName("获取分类详情 - 不存在")
    void testGetCategoryById_NotFound() {
        assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(9999L);
        });
    }

    @Test
    @DisplayName("更新分类 - 成功")
    void testUpdateCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("更新后的Java");
        request.setDescription("更新后的描述");

        CategoryResponse response = categoryService.updateCategory(1L, request);

        assertNotNull(response);
        assertEquals("更新后的Java", response.getName());
        assertEquals("更新后的描述", response.getDescription());
    }

    @Test
    @DisplayName("删除分类 - 成功")
    void testDeleteCategory_Success() {
        assertDoesNotThrow(() -> {
            categoryService.deleteCategory(3L); // 删除没有文章关联的分类
        });
    }
}
