package com.cuit.blog.service;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.dto.response.FollowUserResponse;

// 关注服务接口
public interface FollowService {

    /**
     * 关注用户
     *
     * @param followerId  关注者ID（当前用户）
     * @param followingId 被关注者ID
     */
    void followUser(Long followerId, Long followingId);

    /**
     * 取消关注
     *
     * @param followerId  关注者ID（当前用户）
     * @param followingId 被关注者ID
     */
    void unfollowUser(Long followerId, Long followingId);

    /**
     * 判断是否已关注
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 是否已关注
     */
    boolean isFollowing(Long followerId, Long followingId);

    /**
     * 获取用户的粉丝列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页粉丝列表
     */
    PageResult<FollowUserResponse> getFollowers(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的关注列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页关注列表
     */
    PageResult<FollowUserResponse> getFollowing(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的粉丝数
     *
     * @param userId 用户ID
     * @return 粉丝数
     */
    Long getFollowersCount(Long userId);

    /**
     * 获取用户的关注数
     *
     * @param userId 用户ID
     * @return 关注数
     */
    Long getFollowingCount(Long userId);
}
