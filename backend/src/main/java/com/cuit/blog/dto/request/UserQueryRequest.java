package com.cuit.blog.dto.request;

import lombok.Data;

// 用户查询请求 DTO
@Data
public class UserQueryRequest {

    // 当前页码，默认第1页
    private Integer pageNum = 1;

    // 每页数量，默认10条
    private Integer pageSize = 10;

    // 模糊搜索关键字（用户名/昵称/邮箱）
    private String keyword;

    // 角色筛选
    private String role;
}
