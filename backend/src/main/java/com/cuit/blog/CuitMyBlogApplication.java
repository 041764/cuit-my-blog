package com.cuit.blog;

import com.cuit.blog.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// 博客系统启动类
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class CuitMyBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CuitMyBlogApplication.class, args);
    }
}
