package com.cuit.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.dto.request.ArticleCreateRequest;
import com.cuit.blog.dto.request.ArticleQueryRequest;
import com.cuit.blog.dto.request.ArticleUpdateRequest;
import com.cuit.blog.dto.response.ArticleListResponse;
import com.cuit.blog.dto.response.ArticleResponse;
import com.cuit.blog.dto.response.CategoryResponse;
import com.cuit.blog.entity.Article;
import com.cuit.blog.entity.Category;
import com.cuit.blog.entity.User;
import com.cuit.blog.mapper.ArticleMapper;
import com.cuit.blog.mapper.CategoryMapper;
import com.cuit.blog.mapper.UserMapper;
import com.cuit.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

// 文章服务实现类
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleResponse createArticle(Long userId, ArticleCreateRequest request) {
        // 验证分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 创建文章
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategoryId(request.getCategoryId());
        article.setCoverImage(request.getCoverImage());
        article.setSummary(request.getSummary());

        // 如果没有提供摘要，自动从内容中提取
        if (!StringUtils.hasText(article.getSummary()) && StringUtils.hasText(request.getContent())) {
            String content = request.getContent();
            article.setSummary(content.length() > 200 ? content.substring(0, 200) + "..." : content);
        }

        articleMapper.insert(article);

        return getArticleById(article.getId());
    }

    @Override
    public ArticleResponse getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        return convertToDetailResponse(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleResponse updateArticle(Long id, Long userId, ArticleUpdateRequest request) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 验证是否为文章作者
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 如果更新分类，验证分类是否存在
        if (request.getCategoryId() != null) {
            Category category = categoryMapper.selectById(request.getCategoryId());
            if (category == null) {
                throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
            }
            article.setCategoryId(request.getCategoryId());
        }

        // 更新文章信息
        if (StringUtils.hasText(request.getTitle())) {
            article.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            article.setContent(request.getContent());
        }
        if (request.getCoverImage() != null) {
            article.setCoverImage(request.getCoverImage());
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }

        articleMapper.updateById(article);

        return getArticleById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id, Long userId, boolean isAdmin) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        boolean isArticleAuthor = article.getUserId().equals(userId);

        // 非管理员必须是作者本人才有权限
        if (!isAdmin && !isArticleAuthor) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        if (isAdmin && !isArticleAuthor) {
            User targetAuthor = userMapper.selectById(article.getUserId());
            if (targetAuthor != null && "ADMIN".equalsIgnoreCase(targetAuthor.getRole())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        }

        // 删除文章
        articleMapper.deleteById(id);
    }

    @Override
    public PageResult<ArticleListResponse> getArticleList(ArticleQueryRequest request) {
        int pageNum = request.getPageNum() == null ? 1 : request.getPageNum();
        int pageSize = request.getPageSize() == null ? 10 : request.getPageSize();

        Page<Article> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        // 按分类筛选
        if (request.getCategoryId() != null) {
            queryWrapper.eq(Article::getCategoryId, request.getCategoryId());
        }

        // 按用户筛选
        if (request.getUserId() != null) {
            queryWrapper.eq(Article::getUserId, request.getUserId());
        }

        // 按关键词搜索标题/摘要/内容
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getSummary, keyword)
                    .or()
                    .like(Article::getContent, keyword)
            );
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(Article::getCreatedAt);

        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        List<ArticleListResponse> list = articlePage.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());

        return PageResult.of(
                articlePage.getCurrent(),
                articlePage.getSize(),
                articlePage.getTotal(),
                list
        );
    }

    @Override
    public PageResult<ArticleListResponse> getArticlesByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        // 验证分类是否存在
        if (!categoryMapper.exists(new LambdaQueryWrapper<Category>().eq(Category::getId, categoryId))) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getCategoryId, categoryId)
                .orderByDesc(Article::getCreatedAt);

        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        List<ArticleListResponse> list = articlePage.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());

        return PageResult.of(
                articlePage.getCurrent(),
                articlePage.getSize(),
                articlePage.getTotal(),
                list
        );
    }

    @Override
    public PageResult<ArticleListResponse> getArticlesByUser(Long userId, String keyword, Integer pageNum, Integer pageSize) {
        // 验证用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        int resolvedPageNum = pageNum == null ? 1 : pageNum;
        int resolvedPageSize = pageSize == null ? 10 : pageSize;

        Page<Article> page = new Page<>(resolvedPageNum, resolvedPageSize);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getUserId, userId);

        if (StringUtils.hasText(keyword)) {
            String trimmed = keyword.trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(Article::getTitle, trimmed)
                    .or()
                    .like(Article::getSummary, trimmed)
                    .or()
                    .like(Article::getContent, trimmed)
            );
        }

        queryWrapper.orderByDesc(Article::getCreatedAt);

        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        List<ArticleListResponse> list = articlePage.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());

        return PageResult.of(
                articlePage.getCurrent(),
                articlePage.getSize(),
                articlePage.getTotal(),
                list
        );
    }

    // 将文章实体转换为详情响应DTO
    private ArticleResponse convertToDetailResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(article, response);

        // 获取分类信息
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            BeanUtils.copyProperties(category, categoryResponse);
            response.setCategory(categoryResponse);
        }

        // 获取作者信息
        User author = userMapper.selectById(article.getUserId());
        if (author != null) {
            ArticleResponse.AuthorInfo authorInfo = new ArticleResponse.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatar(author.getAvatar());
            response.setAuthor(authorInfo);
        }

        return response;
    }

    // 将文章实体转换为列表响应DTO
    private ArticleListResponse convertToListResponse(Article article) {
        ArticleListResponse response = new ArticleListResponse();
        BeanUtils.copyProperties(article, response);

        // 获取分类信息
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            BeanUtils.copyProperties(category, categoryResponse);
            response.setCategory(categoryResponse);
        }

        // 获取作者信息
        User author = userMapper.selectById(article.getUserId());
        if (author != null) {
            ArticleResponse.AuthorInfo authorInfo = new ArticleResponse.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatar(author.getAvatar());
            response.setAuthor(authorInfo);
        }

        return response;
    }
}
