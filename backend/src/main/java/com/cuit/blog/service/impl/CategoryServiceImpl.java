package com.cuit.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.dto.CategoryArticleCountDTO;
import com.cuit.blog.dto.request.CategoryRequest;
import com.cuit.blog.dto.response.CategoryResponse;
import com.cuit.blog.entity.Article;
import com.cuit.blog.entity.Category;
import com.cuit.blog.mapper.ArticleMapper;
import com.cuit.blog.mapper.CategoryMapper;
import com.cuit.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 分类服务实现类
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, request.getName());
        if (categoryMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        // 创建分类
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        categoryMapper.insert(category);

        CategoryResponse response = convertToResponse(category);
        response.setArticleCount(0L);
        return response;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryMapper.selectList(null);
        if (categories.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> articleCountMap = buildArticleCountMap();

        return categories.stream()
                .map(category -> {
                    CategoryResponse response = convertToResponse(category);
                    response.setArticleCount(articleCountMap.getOrDefault(category.getId(), 0L));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        CategoryResponse response = convertToResponse(category);
        response.setArticleCount(countArticlesByCategoryId(id));
        return response;
    }

    @Override
    public boolean existsById(Long id) {
        return categoryMapper.selectById(id) != null;
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 检查分类名称是否已被其他分类使用
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, request.getName())
                .ne(Category::getId, id);
        if (categoryMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        // 更新分类信息
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryMapper.updateById(category);

        CategoryResponse response = convertToResponse(category);
        response.setArticleCount(countArticlesByCategoryId(id));
        return response;
    }

    @Override
    public void deleteCategory(Long id) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 删除分类
        categoryMapper.deleteById(id);
    }

    // 将实体转换为响应DTO
    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }

    // 构建分类文章数量映射
    private Map<Long, Long> buildArticleCountMap() {
        List<CategoryArticleCountDTO> stats = articleMapper.selectCategoryArticleCounts();
        if (stats == null || stats.isEmpty()) {
            return Map.of();
        }
        return stats.stream()
                .collect(Collectors.toMap(CategoryArticleCountDTO::getCategoryId, CategoryArticleCountDTO::getArticleCount));
    }

    // 统计单个分类的文章数量
    private long countArticlesByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getCategoryId, categoryId);
        return articleMapper.selectCount(queryWrapper);
    }
}
