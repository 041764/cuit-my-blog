package com.cuit.blog.controller;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.common.result.Result;
import com.cuit.blog.common.utils.SecurityUtils;
import com.cuit.blog.dto.response.FollowUserResponse;
import com.cuit.blog.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 关注控制器
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 关注用户
     *
     * @param currentUserId 当前用户ID
     * @param userId        被关注用户ID
     * @return 操作结果
     */
    @PostMapping("/{userId}")
    public Result<Void> followUser(
            @PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getRequiredUserId();
        followService.followUser(currentUserId, userId);
        return Result.success();
    }

    /**
     * 取消关注
     *
     * @param currentUserId 当前用户ID
     * @param userId        被关注用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    public Result<Void> unfollowUser(
            @PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getRequiredUserId();
        followService.unfollowUser(currentUserId, userId);
        return Result.success();
    }

    /**
     * 判断是否已关注某用户
     *
     * @param currentUserId 当前用户ID
     * @param userId        目标用户ID
     * @return 是否已关注
     */
    @GetMapping("/{userId}/status")
    public Result<Boolean> isFollowing(
            @PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getRequiredUserId();
        boolean following = followService.isFollowing(currentUserId, userId);
        return Result.success(following);
    }

    /**
     * 获取我关注的博主列表
     *
     * @param currentUserId 当前用户ID
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return 分页关注列表
     */
    @GetMapping("/my/following")
    public Result<PageResult<FollowUserResponse>> getMyFollowing(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long currentUserId = SecurityUtils.getRequiredUserId();
        PageResult<FollowUserResponse> result = followService.getFollowing(currentUserId, pageNum, pageSize);
        return Result.success(result);
    }
}
