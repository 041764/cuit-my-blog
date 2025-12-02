package com.cuit.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.common.utils.PasswordEncoder;
import com.cuit.blog.dto.request.UserLoginRequest;
import com.cuit.blog.dto.request.UserProfileUpdateRequest;
import com.cuit.blog.dto.request.UserQueryRequest;
import com.cuit.blog.dto.request.UserRegisterRequest;
import com.cuit.blog.dto.response.LoginResponse;
import com.cuit.blog.dto.response.UserResponse;
import com.cuit.blog.entity.User;
import com.cuit.blog.mapper.UserMapper;
import com.cuit.blog.security.JwtService;
import com.cuit.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

// 用户服务实现类
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Value("${user.default-avatar:/uploads/default/avatar.png}")
    private String defaultAvatar;

    // 默认用户角色
    private static final String DEFAULT_ROLE = "USER";

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }

        // 检查邮箱是否已存在
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXIST);
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(DEFAULT_ROLE);
        user.setAvatar(defaultAvatar);

        // 保存用户
        userMapper.insert(user);

        return convertToResponse(user);
    }

    @Override
    public LoginResponse login(UserLoginRequest request) {
        // 根据用户名查询用户
        User user = getUserByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        UserResponse userResponse = convertToResponse(user);
        String token = jwtService.generateToken(user);
        LoginResponse response = new LoginResponse(userResponse, token, jwtService.getExpiration(), "登录成功");
        response.setTokenType(jwtService.getTokenPrefixWithSpace().trim());
        return response;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新昵称
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        // 更新个人简介
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        userMapper.updateById(user);
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setAvatar((avatarUrl == null || avatarUrl.isBlank()) ? defaultAvatar : avatarUrl);
        userMapper.updateById(user);
        return convertToResponse(user);
    }

    // 检查用户名是否存在
    private boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    // 检查邮箱是否存在
    private boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }

    // 根据用户名获取用户
    private User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    // 将User实体转换为UserResponse
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        String avatar = user.getAvatar();
        response.setAvatar((avatar == null || avatar.isBlank()) ? defaultAvatar : avatar);
        response.setBio(user.getBio());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    @Override
    public PageResult<UserResponse> getUserPage(UserQueryRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword();
            wrapper.and(q -> q.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword));
        }

        if (StringUtils.hasText(request.getRole())) {
            wrapper.eq(User::getRole, request.getRole().toUpperCase());
        }

        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> userPage = userMapper.selectPage(page, wrapper);
        List<UserResponse> list = userPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PageResult.of(
                userPage.getCurrent(),
                userPage.getSize(),
                userPage.getTotal(),
                list
        );
    }
}
