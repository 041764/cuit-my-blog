package com.cuit.blog.service;

import com.cuit.blog.dto.request.UserLoginRequest;
import com.cuit.blog.dto.request.UserProfileUpdateRequest;
import com.cuit.blog.dto.request.UserRegisterRequest;
import com.cuit.blog.dto.response.LoginResponse;
import com.cuit.blog.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// 用户服务测试类
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("用户服务测试")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("注册用户 - 成功")
    void testRegister_Success() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("serviceTestUser");
        request.setPassword("password123");
        request.setEmail("servicetest@test.com");
        request.setNickname("服务测试用户");

        UserResponse response = userService.register(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("serviceTestUser", response.getUsername());
        assertEquals("servicetest@test.com", response.getEmail());
        assertEquals("服务测试用户", response.getNickname());
        assertEquals("USER", response.getRole());
    }

    @Test
    @DisplayName("注册用户 - 用户名已存在")
    void testRegister_UsernameExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser1"); // 测试数据中已存在
        request.setPassword("password123");
        request.setEmail("newunique@test.com");

        assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    @DisplayName("注册用户 - 邮箱已存在")
    void testRegister_EmailExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuniqueuser");
        request.setPassword("password123");
        request.setEmail("test1@test.com"); // 测试数据中已存在

        assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void testLogin_Success() {
        // 先注册一个用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("loginTestUser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("logintest@test.com");
        userService.register(registerRequest);

        // 测试登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("loginTestUser");
        loginRequest.setPassword("password123");

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertEquals("loginTestUser", response.getUser().getUsername());
    }

    @Test
    @DisplayName("用户登录 - 用户名不存在")
    void testLogin_UsernameNotFound() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("nonexistentuser");
        request.setPassword("password123");

        assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        // 先注册一个用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("wrongPwdUser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("wrongpwd@test.com");
        userService.register(registerRequest);

        // 测试错误密码登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("wrongPwdUser");
        loginRequest.setPassword("wrongpassword");

        assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("获取用户信息 - 成功")
    void testGetUserById_Success() {
        UserResponse response = userService.getUserById(2L);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("testuser1", response.getUsername());
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void testGetUserById_NotFound() {
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(9999L);
        });
    }

    @Test
    @DisplayName("更新个人资料 - 成功")
    void testUpdateProfile_Success() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setNickname("更新后的昵称");
        request.setBio("更新后的简介");

        UserResponse response = userService.updateProfile(2L, request);

        assertNotNull(response);
        assertEquals("更新后的昵称", response.getNickname());
        assertEquals("更新后的简介", response.getBio());
    }
}
