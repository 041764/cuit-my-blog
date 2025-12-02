package com.cuit.blog.controller;

import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.common.result.Result;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.common.utils.SecurityUtils;
import com.cuit.blog.dto.request.UserLoginRequest;
import com.cuit.blog.dto.request.UserProfileUpdateRequest;
import com.cuit.blog.dto.request.UserQueryRequest;
import com.cuit.blog.dto.request.UserRegisterRequest;
import com.cuit.blog.dto.response.ArticleListResponse;
import com.cuit.blog.dto.response.FollowUserResponse;
import com.cuit.blog.dto.response.LoginResponse;
import com.cuit.blog.dto.response.UserResponse;
import com.cuit.blog.service.ArticleService;
import com.cuit.blog.service.FileService;
import com.cuit.blog.service.FollowService;
import com.cuit.blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 用户控制器
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileService fileService;
    private final ArticleService articleService;
    private final FollowService followService;

    /**
     * 管理员获取用户列表
     *
     * @param request 查询条件
     * @return 用户分页列表
     */
    @GetMapping
    public Result<PageResult<UserResponse>> getUsers(UserQueryRequest request) {
        if (!SecurityUtils.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        PageResult<UserResponse> result = userService.getUserPage(request);
        return Result.success(result);
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.register(request);
        return Result.success(response);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    /**
     * 获取当前登录用户的个人资料
     *
     * @return 用户信息
     */
    @GetMapping("/profile")
    public Result<UserResponse> getProfile() {
        Long userId = SecurityUtils.getRequiredUserId();
        UserResponse response = userService.getUserById(userId);
        return Result.success(response);
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return Result.success(response);
    }

    /**
     * 更新个人资料
     * 注意：实际项目中userId应从登录态（如JWT Token）中获取
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public Result<UserResponse> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        Long userId = SecurityUtils.getRequiredUserId();
        UserResponse response = userService.updateProfile(userId, request);
        return Result.success(response);
    }

    /**
     * 上传头像
     * 注意：实际项目中userId应从登录态（如JWT Token）中获取
     * @param file   头像文件
     * @return 更新后的用户信息
     */
    @PostMapping("/avatar")
    public Result<UserResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        // 上传头像文件
        String avatarUrl = fileService.uploadAvatar(file);
        // 更新用户头像URL
        Long userId = SecurityUtils.getRequiredUserId();
        UserResponse response = userService.updateAvatar(userId, avatarUrl);
        return Result.success(response);
    }

    /**
     * 获取用户的文章列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页文章列表
     */
    @GetMapping("/{userId}/articles")
    public Result<PageResult<ArticleListResponse>> getUserArticles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        PageResult<ArticleListResponse> result = articleService.getArticlesByUser(userId, keyword, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取用户的关注列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页关注列表
     */
    @GetMapping("/{userId}/following")
    public Result<PageResult<FollowUserResponse>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<FollowUserResponse> result = followService.getFollowing(userId, pageNum, pageSize);
        return Result.success(result);
    }
}
