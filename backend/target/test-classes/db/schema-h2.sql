-- ==============================================
-- 博客系统测试数据库初始化脚本 (SQLite)
-- ==============================================

PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    nickname TEXT,
    avatar TEXT,
    bio TEXT,
    role TEXT NOT NULL DEFAULT 'USER',
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS article (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    category_id INTEGER,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    cover_image TEXT,
    summary TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_article_user_id ON article (user_id);
CREATE INDEX IF NOT EXISTS idx_article_category_id ON article (category_id);
CREATE INDEX IF NOT EXISTS idx_article_created_at ON article (created_at);

CREATE TABLE IF NOT EXISTS comment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    article_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    parent_id INTEGER,
    reply_to_user_id INTEGER,
    content TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (article_id) REFERENCES article (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comment (id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to_user_id) REFERENCES user (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_comment_article_id ON comment (article_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment (user_id);
CREATE INDEX IF NOT EXISTS idx_comment_parent_id ON comment (parent_id);

CREATE TABLE IF NOT EXISTS user_follow (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    follower_id INTEGER NOT NULL,
    following_id INTEGER NOT NULL,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    UNIQUE (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES user (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_follow_following_id ON user_follow (following_id);
