package com.cuit.blog.service;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.dto.request.UserLoginRequest;
import com.cuit.blog.dto.request.UserProfileUpdateRequest;
import com.cuit.blog.dto.request.UserQueryRequest;
import com.cuit.blog.dto.request.UserRegisterRequest;
import com.cuit.blog.dto.response.LoginResponse;
import com.cuit.blog.dto.response.UserResponse;

// 用户服务接口
public interface UserService {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    UserResponse register(UserRegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(UserLoginRequest request);

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserResponse getUserById(Long id);

    /**
     * 更新用户资料
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    UserResponse updateProfile(Long userId, UserProfileUpdateRequest request);

    /**
     * 更新用户头像
     *
     * @param userId    用户ID
     * @param avatarUrl 头像URL
     * @return 更新后的用户信息
     */
    UserResponse updateAvatar(Long userId, String avatarUrl);

    /**
     * 分页查询用户（管理员）
     *
     * @param request 查询请求
     * @return 用户分页结果
     */
    PageResult<UserResponse> getUserPage(UserQueryRequest request);
}
