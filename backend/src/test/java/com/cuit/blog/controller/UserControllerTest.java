package com.cuit.blog.controller;

import com.cuit.blog.common.result.Result;
import com.cuit.blog.dto.request.UserLoginRequest;
import com.cuit.blog.dto.request.UserProfileUpdateRequest;
import com.cuit.blog.dto.request.UserRegisterRequest;
import com.cuit.blog.testutil.TestTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 用户控制器测试类
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("用户模块测试")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTokenProvider tokenProvider;

    @Test
    @DisplayName("用户注册 - 成功")
    void testRegister_Success() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("newuser@test.com");
        request.setNickname("新用户");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@test.com"));
    }

    @Test
    @DisplayName("用户注册 - 用户名为空")
    void testRegister_UsernameEmpty() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("");
        request.setPassword("password123");
        request.setEmail("test@test.com");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("用户注册 - 密码太短")
    void testRegister_PasswordTooShort() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("123");
        request.setEmail("test@test.com");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("用户注册 - 邮箱格式错误")
    void testRegister_InvalidEmail() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("invalid-email");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() throws Exception {
        // 测试数据中的密码是加密的，与 password123 不匹配
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("testuser1");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(1004));
    }

    @Test
    @DisplayName("用户登录 - 用户名为空")
    void testLogin_UsernameEmpty() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取用户信息 - 成功")
    void testGetUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser1"));
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/9999"))
                .andDo(print())
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(1003));
    }

    @Test
    @DisplayName("更新个人资料 - 成功")
    void testUpdateProfile_Success() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setNickname("更新后的昵称");
        request.setBio("更新后的简介");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", tokenProvider.bearerToken(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("更新后的昵称"));
    }

    @Test
    @DisplayName("获取用户文章列表")
    void testGetUserArticles() throws Exception {
        mockMvc.perform(get("/api/users/2/articles")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取用户粉丝列表")
    void testGetUserFollowers() throws Exception {
        mockMvc.perform(get("/api/users/2/followers")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取用户关注列表")
    void testGetUserFollowing() throws Exception {
        mockMvc.perform(get("/api/users/3/following")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
