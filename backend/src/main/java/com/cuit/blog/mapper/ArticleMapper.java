package com.cuit.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.blog.dto.CategoryArticleCountDTO;
import com.cuit.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 文章 Mapper 接口
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

	/**
	 * 统计每个分类下的文章数量
	 *
	 * @return 分类与文章数量的映射列表
	 */
	@Select("SELECT category_id AS categoryId, COUNT(*) AS articleCount FROM article GROUP BY category_id")
	List<CategoryArticleCountDTO> selectCategoryArticleCounts();
}
