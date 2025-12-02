package com.cuit.blog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

// 应用程序启动测试
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("应用启动测试")
class CuitMyBlogApplicationTests {

    @Test
    @DisplayName("应用程序上下文加载成功")
    void contextLoads() {
        // 如果应用程序上下文加载成功，测试将通过
        assertTrue(true);
    }
}
