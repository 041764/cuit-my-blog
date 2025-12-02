package com.cuit.blog.controller;

import com.cuit.blog.common.result.Result;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.common.utils.SecurityUtils;
import com.cuit.blog.dto.request.CategoryRequest;
import com.cuit.blog.dto.response.CategoryResponse;
import com.cuit.blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 分类控制器
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类列表
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 创建分类（仅管理员）
     *
     * @param role    用户角色
     * @param request 创建分类请求
     * @return 分类信息
     */
    @PostMapping
    public Result<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        if (!SecurityUtils.isAdmin()) {
            return Result.failed(ResultCode.FORBIDDEN, "没有权限创建分类");
        }
        CategoryResponse response = categoryService.createCategory(request);
        return Result.success(response);
    }

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/{id}")
    public Result<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return Result.success(response);
    }

    /**
     * 更新分类（仅管理员）
     *
     * @param id      分类ID
     * @param role    用户角色
     * @param request 更新分类请求
     * @return 分类信息
     */
    @PutMapping("/{id}")
    public Result<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        if (!SecurityUtils.isAdmin()) {
            return Result.failed(ResultCode.FORBIDDEN, "没有权限更新分类");
        }
        CategoryResponse response = categoryService.updateCategory(id, request);
        return Result.success(response);
    }

    /**
     * 删除分类（仅管理员）
     *
     * @param id   分类ID
     * @param role 用户角色
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(
            @PathVariable Long id) {
        if (!SecurityUtils.isAdmin()) {
            return Result.failed(ResultCode.FORBIDDEN, "没有权限删除分类");
        }
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
