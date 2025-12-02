-- ==============================================
-- 测试数据初始化脚本
-- ==============================================

-- 初始化测试用户
-- 密码均为: password123 (明文), 这里使用简单的加密值用于测试
INSERT INTO user (id, username, password, email, nickname, role, bio) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@test.com', '管理员', 'ADMIN', '系统管理员'),
(2, 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test1@test.com', '测试用户1', 'USER', '我是测试用户1'),
(3, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test2@test.com', '测试用户2', 'USER', '我是测试用户2'),
(4, 'admin_mgr', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin2@test.com', '管理员2号', 'ADMIN', '第二位管理员');
INSERT INTO category (id, name, description) VALUES
(1, 'Java', 'Java编程相关'),
(2, 'Spring', 'Spring框架相关'),
(3, '前端', '前端开发技术');

INSERT INTO article (id, user_id, category_id, title, content, summary) VALUES
(1, 2, 1, '第一篇测试文章', '# 第一篇文章内容\n这是测试内容。', '这是第一篇测试文章的摘要'),
(2, 2, 2, '第二篇测试文章', '# Spring Boot入门\n这是Spring Boot的内容。', 'Spring Boot入门教程'),
(3, 3, 1, '第三篇测试文章', '# Java基础\n这是Java基础的内容。', 'Java基础教程'),
(4, 4, 2, '管理员专属文章', '# 管理员文章\n只有管理员才看得到。', '管理员自己的文章'),
(5, 2, 3, '前端性能实践', '# 前端性能\n优化要点。', '全文搜索进阶指南');
INSERT INTO comment (id, article_id, user_id, parent_id, reply_to_user_id, content) VALUES
(1, 1, 3, NULL, NULL, '这是一条顶级评论'),
(2, 1, 2, 1, 3, '这是对顶级评论的回复');
INSERT INTO user_follow (id, follower_id, following_id) VALUES
(1, 3, 2);
