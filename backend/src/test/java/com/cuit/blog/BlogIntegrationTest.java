package com.cuit.blog;

import com.cuit.blog.dto.request.*;
import com.cuit.blog.testutil.TestTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 博客系统端到端集成测试
 * 模拟完整的用户操作流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("博客系统集成测试")
public class BlogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTokenProvider tokenProvider;

    private Long testUserId;
    private Long testArticleId;
    private Long testCommentId;
    private String testUserToken;
    private String otherUserToken;

    @BeforeAll
    void setUpTokens() {
        otherUserToken = tokenProvider.bearerToken(2L);
    }

    @Test
    @Order(1)
    @DisplayName("完整流程测试 - 1. 新用户注册")
    void test01_UserRegister() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("integrationuser");
        request.setPassword("password123");
        request.setEmail("integration@test.com");
        request.setNickname("集成测试用户");

        MvcResult result = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("integrationuser"))
                .andReturn();

        // 获取用户ID用于后续测试
        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        testUserId = jsonNode.get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("完整流程测试 - 2. 用户登录")
    void test02_UserLogin() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("integrationuser");
        request.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        String token = jsonNode.get("data").get("token").asText();
        String tokenType = jsonNode.get("data").get("tokenType").asText();
        testUserToken = tokenType + " " + token;
    }

    @Test
    @Order(3)
    @DisplayName("完整流程测试 - 3. 更新个人资料")
    void test03_UpdateProfile() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setNickname("更新后的昵称");
        request.setBio("这是我的个人简介");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("更新后的昵称"));
    }

    @Test
    @Order(4)
    @DisplayName("完整流程测试 - 4. 发布文章")
    void test04_CreateArticle() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("集成测试文章");
        request.setContent("# 这是集成测试\n\n这是文章内容。");
        request.setCategoryId(1L);
        request.setSummary("集成测试文章摘要");

        MvcResult result = mockMvc.perform(post("/api/articles")
                .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("集成测试文章"))
                .andReturn();

        // 获取文章ID用于后续测试
        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        testArticleId = jsonNode.get("data").get("id").asLong();
    }

    @Test
    @Order(5)
    @DisplayName("完整流程测试 - 5. 获取文章详情")
    void test05_GetArticleDetail() throws Exception {
        mockMvc.perform(get("/api/articles/" + testArticleId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("集成测试文章"));
    }

    @Test
    @Order(6)
    @DisplayName("完整流程测试 - 6. 发表评论")
    void test06_CreateComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(testArticleId);
        request.setContent("这是集成测试评论");

        MvcResult result = mockMvc.perform(post("/api/comments")
                .header("Authorization", otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        // 获取评论ID用于后续测试
        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        testCommentId = jsonNode.get("data").get("id").asLong();
    }

    @Test
    @Order(7)
    @DisplayName("完整流程测试 - 7. 获取文章评论")
    void test07_GetArticleComments() throws Exception {
        mockMvc.perform(get("/api/comments/article/" + testArticleId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

        @Test
        @Order(8)
        @DisplayName("完整流程测试 - 8. 关注作者")
        void test08_FollowAuthor() throws Exception {
        mockMvc.perform(post("/api/follows/" + testUserId)
                .header("Authorization", otherUserToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
        @Order(9)
        @DisplayName("完整流程测试 - 9. 检查关注状态")
        void test09_CheckFollowStatus() throws Exception {
        mockMvc.perform(get("/api/follows/" + testUserId + "/status")
                .header("Authorization", otherUserToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
        @Order(10)
        @DisplayName("完整流程测试 - 10. 更新文章")
        void test10_UpdateArticle() throws Exception {
        ArticleUpdateRequest request = new ArticleUpdateRequest();
        request.setTitle("更新后的集成测试文章");
        request.setContent("# 更新后的内容\n\n内容已更新。");

        mockMvc.perform(put("/api/articles/" + testArticleId)
                .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("更新后的集成测试文章"));
    }

    @Test
        @Order(11)
        @DisplayName("完整流程测试 - 11. 取消关注")
        void test11_UnfollowAuthor() throws Exception {
        mockMvc.perform(delete("/api/follows/" + testUserId)
                .header("Authorization", otherUserToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
        @Order(12)
        @DisplayName("完整流程测试 - 12. 删除评论")
        void test12_DeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/" + testCommentId)
                .header("Authorization", otherUserToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
        @Order(13)
        @DisplayName("完整流程测试 - 13. 删除文章")
        void test13_DeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/" + testArticleId)
                .header("Authorization", testUserToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
