package com.cuit.blog.testutil;

import com.cuit.blog.entity.User;
import com.cuit.blog.mapper.UserMapper;
import com.cuit.blog.security.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

// 基于真实 JWT 服务的测试用令牌生成器，避免在测试中硬编码 token。
@Component
public class TestTokenProvider {

    private final JwtService jwtService;
    private final UserMapper userMapper;

    public TestTokenProvider(JwtService jwtService, UserMapper userMapper) {
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public String bearerToken(long userId) {
        User user = userMapper.selectById(userId);
        Assert.notNull(user, "测试用户不存在: " + userId);
        String token = jwtService.generateToken(user);
        return jwtService.getTokenPrefixWithSpace() + token;
    }
}
