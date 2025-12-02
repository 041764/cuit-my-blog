package com.cuit.blog.service;

import com.cuit.blog.dto.request.CategoryRequest;
import com.cuit.blog.dto.response.CategoryResponse;

import java.util.List;

// 分类服务接口
public interface CategoryService {

    /**
     * 创建分类（管理员）
     *
     * @param request 创建分类请求
     * @return 分类信息
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * 获取所有分类列表
     *
     * @return 分类列表
     */
    List<CategoryResponse> getAllCategories();

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * 检查分类是否存在
     *
     * @param id 分类ID
     * @return 是否存在
     */
    boolean existsById(Long id);

    /**
     * 更新分类（管理员）
     *
     * @param id      分类ID
     * @param request 更新分类请求
     * @return 分类信息
     */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /**
     * 删除分类（管理员）
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}
