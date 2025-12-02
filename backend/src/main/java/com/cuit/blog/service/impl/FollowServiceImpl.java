package com.cuit.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.dto.response.FollowUserResponse;
import com.cuit.blog.entity.User;
import com.cuit.blog.entity.UserFollow;
import com.cuit.blog.mapper.UserFollowMapper;
import com.cuit.blog.mapper.UserMapper;
import com.cuit.blog.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 关注服务实现类
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followUser(Long followerId, Long followingId) {
        // 不能关注自己
        if (followerId.equals(followingId)) {
            throw new BusinessException(ResultCode.CANNOT_FOLLOW_SELF);
        }

        // 检查被关注用户是否存在
        User followingUser = userMapper.selectById(followingId);
        if (followingUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查是否已经关注
        if (isFollowing(followerId, followingId)) {
            throw new BusinessException(ResultCode.ALREADY_FOLLOWED);
        }

        // 创建关注记录
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowMapper.insert(follow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long followerId, Long followingId) {
        // 检查被关注用户是否存在
        User followingUser = userMapper.selectById(followingId);
        if (followingUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查是否已经关注
        if (!isFollowing(followerId, followingId)) {
            throw new BusinessException(ResultCode.NOT_FOLLOWED);
        }

        // 删除关注记录
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getFollowerId, followerId)
                .eq(UserFollow::getFollowingId, followingId);
        userFollowMapper.delete(queryWrapper);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getFollowerId, followerId)
                .eq(UserFollow::getFollowingId, followingId);
        return userFollowMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public PageResult<FollowUserResponse> getFollowers(Long userId, Integer pageNum, Integer pageSize) {
        // 分页查询粉丝记录（关注者为粉丝）
        Page<UserFollow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getFollowingId, userId)
                .orderByDesc(UserFollow::getCreatedAt);

        Page<UserFollow> followPage = userFollowMapper.selectPage(page, queryWrapper);

        // 转换为响应DTO
        List<FollowUserResponse> list = followPage.getRecords().stream()
                .map(follow -> convertToFollowerResponse(follow))
                .filter(response -> response != null)
                .collect(Collectors.toList());

        return PageResult.of(
                followPage.getCurrent(),
                followPage.getSize(),
                followPage.getTotal(),
                list
        );
    }

    @Override
    public PageResult<FollowUserResponse> getFollowing(Long userId, Integer pageNum, Integer pageSize) {
        // 分页查询关注记录（被关注者为关注的人）
        Page<UserFollow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getFollowerId, userId)
                .orderByDesc(UserFollow::getCreatedAt);

        Page<UserFollow> followPage = userFollowMapper.selectPage(page, queryWrapper);

        // 转换为响应DTO
        List<FollowUserResponse> list = followPage.getRecords().stream()
                .map(follow -> convertToFollowingResponse(follow))
                .filter(response -> response != null)
                .collect(Collectors.toList());

        return PageResult.of(
                followPage.getCurrent(),
                followPage.getSize(),
                followPage.getTotal(),
                list
        );
    }

    @Override
    public Long getFollowersCount(Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getFollowingId, userId);
        return userFollowMapper.selectCount(queryWrapper);
    }

    @Override
    public Long getFollowingCount(Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getFollowerId, userId);
        return userFollowMapper.selectCount(queryWrapper);
    }

    // 将关注记录转换为粉丝响应DTO
    private FollowUserResponse convertToFollowerResponse(UserFollow follow) {
        User user = userMapper.selectById(follow.getFollowerId());
        if (user == null) {
            return null;
        }
        return buildFollowUserResponse(user, follow);
    }

    // 将关注记录转换为关注响应DTO
    private FollowUserResponse convertToFollowingResponse(UserFollow follow) {
        User user = userMapper.selectById(follow.getFollowingId());
        if (user == null) {
            return null;
        }
        return buildFollowUserResponse(user, follow);
    }

    // 构建关注用户响应DTO
    private FollowUserResponse buildFollowUserResponse(User user, UserFollow follow) {
        FollowUserResponse response = new FollowUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setFollowedAt(follow.getCreatedAt());
        return response;
    }
}
