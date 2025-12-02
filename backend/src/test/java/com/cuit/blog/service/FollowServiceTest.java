package com.cuit.blog.service;

import com.cuit.blog.common.result.PageResult;
import com.cuit.blog.dto.response.FollowUserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// 关注服务测试类
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("关注服务测试")
public class FollowServiceTest {

    @Autowired
    private FollowService followService;

    @Test
    @DisplayName("关注用户 - 成功")
    void testFollowUser_Success() {
        assertDoesNotThrow(() -> {
            followService.followUser(2L, 3L); // 用户2关注用户3
        });
    }

    @Test
    @DisplayName("关注用户 - 关注自己失败")
    void testFollowUser_SelfFollow() {
        assertThrows(RuntimeException.class, () -> {
            followService.followUser(2L, 2L);
        });
    }

    @Test
    @DisplayName("关注用户 - 重复关注")
    void testFollowUser_Duplicate() {
        assertThrows(RuntimeException.class, () -> {
            followService.followUser(3L, 2L); // 用户3已经关注用户2
        });
    }

    @Test
    @DisplayName("取消关注 - 成功")
    void testUnfollowUser_Success() {
        assertDoesNotThrow(() -> {
            followService.unfollowUser(3L, 2L); // 用户3取消关注用户2
        });
    }

    @Test
    @DisplayName("取消关注 - 未关注")
    void testUnfollowUser_NotFollowed() {
        assertThrows(RuntimeException.class, () -> {
            followService.unfollowUser(2L, 3L); // 用户2未关注用户3
        });
    }

    @Test
    @DisplayName("判断是否已关注 - 已关注")
    void testIsFollowing_True() {
        boolean result = followService.isFollowing(3L, 2L);
        assertTrue(result);
    }

    @Test
    @DisplayName("判断是否已关注 - 未关注")
    void testIsFollowing_False() {
        boolean result = followService.isFollowing(2L, 3L);
        assertFalse(result);
    }

    @Test
    @DisplayName("获取用户关注列表")
    void testGetFollowing() {
        PageResult<FollowUserResponse> result = followService.getFollowing(3L, 1, 10);

        assertNotNull(result);
        assertNotNull(result.getList());
        assertTrue(result.getTotal() > 0);
    }

    @Test
    @DisplayName("获取用户粉丝列表")
    void testGetFollowers() {
        PageResult<FollowUserResponse> result = followService.getFollowers(2L, 1, 10);

        assertNotNull(result);
        assertNotNull(result.getList());
        assertTrue(result.getTotal() > 0);
    }
}
