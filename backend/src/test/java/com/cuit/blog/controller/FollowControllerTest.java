package com.cuit.blog.controller;

import com.cuit.blog.testutil.TestTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 关注控制器测试类
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("关注模块测试")
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTokenProvider tokenProvider;

    @Test
    @DisplayName("关注用户 - 成功")
    void testFollowUser_Success() throws Exception {
        mockMvc.perform(post("/api/follows/3")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("关注用户 - 关注自己失败")
    void testFollowUser_SelfFollow() throws Exception {
        mockMvc.perform(post("/api/follows/2")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(6003));
    }

    @Test
    @DisplayName("关注用户 - 重复关注")
    void testFollowUser_Duplicate() throws Exception {
        // 测试数据中用户3已经关注了用户2
        mockMvc.perform(post("/api/follows/2")
                .header("Authorization", tokenProvider.bearerToken(3L)))
                .andDo(print())
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(6001));
    }

    @Test
    @DisplayName("取消关注 - 成功")
    void testUnfollowUser_Success() throws Exception {
        // 测试数据中用户3已经关注了用户2
        mockMvc.perform(delete("/api/follows/2")
                .header("Authorization", tokenProvider.bearerToken(3L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("取消关注 - 未关注")
    void testUnfollowUser_NotFollowed() throws Exception {
        mockMvc.perform(delete("/api/follows/3")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(6002));
    }

    @Test
    @DisplayName("判断是否已关注 - 已关注")
    void testIsFollowing_True() throws Exception {
        mockMvc.perform(get("/api/follows/2/status")
                .header("Authorization", tokenProvider.bearerToken(3L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("判断是否已关注 - 未关注")
    void testIsFollowing_False() throws Exception {
        mockMvc.perform(get("/api/follows/3/status")
                .header("Authorization", tokenProvider.bearerToken(2L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("获取我关注的博主列表")
    void testGetMyFollowing() throws Exception {
        mockMvc.perform(get("/api/follows/my/following")
                .header("Authorization", tokenProvider.bearerToken(3L))
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
}
