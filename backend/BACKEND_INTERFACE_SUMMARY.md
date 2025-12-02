# 后端接口汇总报告

## 1. 概述
后端基础框架及主要功能模块已开发完成并经过测试。本报告旨在汇总后端接口信息，协助前端进行对接开发。

## 2. 基础信息
- **Base URL**: `http://localhost:8080` (默认)
- **日期时间格式**: ISO 8601 (e.g., `2023-10-27T10:00:00`)

## 3. 通用响应结构

### 3.1 基础响应 `Result<T>`
所有接口统一返回此结构：
```json
{
  "code": 200,          // 状态码，200表示成功
  "message": "success", // 提示信息
  "data": { ... },      // 具体数据
  "timestamp": 1698372000000 // 时间戳
}
```

### 3.2 分页响应 `PageResult<T>`
分页接口的 `data` 字段结构：
```json
{
  "pageNum": 1,     // 当前页码
  "pageSize": 10,   // 每页数量
  "total": 100,     // 总记录数
  "pages": 10,      // 总页数
  "list": [ ... ]   // 数据列表
}
```

## 4. 认证与鉴权 (JWT)
系统已切换为基于 JWT 的无状态认证。登录成功后返回的 `LoginResponse` 中包含：

- `tokenType`：默认为 `Bearer`
- `token`：实际的 JWT 访问令牌（Header + Payload 包含 `userId` 与 `role`）
- `expiresIn`：有效期（毫秒）

客户端需在所有需要身份认证的请求中附带 `Authorization: {tokenType} {token}` 头。管理员专用接口需要 `role=ADMIN` 的令牌；普通用户接口使用任意合法登录令牌即可。

> 建议前端在登录后缓存 `user.id`、`user.role` 及 `token`，并在拦截器中自动注入 `Authorization` 头。

## 5. API 接口列表

### 5.1 用户模块 (`/api/users`)

| 方法 | 路径 | 描述 | 请求参数/Body | 响应数据 |
| --- | --- | --- | --- | --- |
| POST | `/register` | 用户注册 | `UserRegisterRequest` | `UserResponse` |
| POST | `/login` | 用户登录 | `UserLoginRequest` | `LoginResponse` |
| GET | `/{id}` | 获取用户信息 | Path: `id` | `UserResponse` |
| PUT | `/profile` | 更新资料 | Header: `Authorization`, Body: `UserProfileUpdateRequest` | `UserResponse` |
| POST | `/avatar` | 上传头像 | Header: `Authorization`, Form: `file` | `UserResponse` |
| GET | `/{userId}/articles` | 用户文章列表 | Path: `userId`, Query: `pageNum`, `pageSize` | `PageResult<ArticleListResponse>` |
| GET | `/{userId}/followers` | 用户粉丝列表 | Path: `userId`, Query: `pageNum`, `pageSize` | `PageResult<FollowUserResponse>` |
| GET | `/{userId}/following` | 用户关注列表 | Path: `userId`, Query: `pageNum`, `pageSize` | `PageResult<FollowUserResponse>` |

### 5.2 文章模块 (`/api/articles`)

| 方法 | 路径 | 描述 | 请求参数/Body | 响应数据 |
| --- | --- | --- | --- | --- |
| POST | `/` | 发布文章 | Header: `Authorization`, Body: `ArticleCreateRequest` | `ArticleResponse` |
| GET | `/` | 文章列表(搜索) | Query: `pageNum`, `pageSize`, `keyword`, `categoryId`, `tagId` | `PageResult<ArticleListResponse>` |
| GET | `/{id}` | 文章详情 | Path: `id` | `ArticleResponse` |
| PUT | `/{id}` | 更新文章 | Header: `Authorization`, Path: `id`, Body: `ArticleUpdateRequest` | `ArticleResponse` |
| DELETE | `/{id}` | 删除文章 | Header: `Authorization` (作者或管理员), Path: `id` | `Void` |
| GET | `/category/{categoryId}` | 按分类查 | Path: `categoryId`, Query: `pageNum`, `pageSize` | `PageResult<ArticleListResponse>` |
| GET | `/tag/{tagId}` | 按标签查 | Path: `tagId`, Query: `pageNum`, `pageSize` | `PageResult<ArticleListResponse>` |

### 5.3 评论模块 (`/api/comments`)

| 方法 | 路径 | 描述 | 请求参数/Body | 响应数据 |
| --- | --- | --- | --- | --- |
| POST | `/` | 发表评论 | Header: `Authorization`, Body: `CommentCreateRequest` | `CommentResponse` |
| GET | `/article/{articleId}` | 文章评论列表 | Path: `articleId` | `List<CommentResponse>` (树形) |
| DELETE | `/{id}` | 删除评论 | Header: `Authorization` (评论作者/文章作者/管理员) | `Void` |

### 5.4 分类与标签模块

| 方法 | 路径 | 描述 | 请求参数/Body | 响应数据 |
| --- | --- | --- | --- | --- |
| GET | `/api/categories` | 所有分类 | - | `List<CategoryResponse>` |
| POST | `/api/categories` | 创建分类(Admin) | Header: `Authorization` (role=ADMIN), Body: `CategoryRequest` | `CategoryResponse` |
| GET | `/api/categories/{id}` | 分类详情 | Path: `id` | `CategoryResponse` |
| PUT | `/api/categories/{id}` | 更新分类(Admin) | Header: `Authorization` (role=ADMIN), Path: `id`, Body: `CategoryRequest` | `CategoryResponse` |
| DELETE | `/api/categories/{id}` | 删除分类(Admin) | Header: `Authorization` (role=ADMIN), Path: `id` | `Void` |
| GET | `/api/tags` | 所有标签 | - | `List<TagResponse>` |
| POST | `/api/tags` | 创建标签(Admin) | Header: `Authorization` (role=ADMIN), Body: `TagRequest` | `TagResponse` |
| GET | `/api/tags/{id}` | 标签详情 | Path: `id` | `TagResponse` |

### 5.5 关注与点赞模块

| 方法 | 路径 | 描述 | 请求参数/Body | 响应数据 |
| --- | --- | --- | --- | --- |
| POST | `/api/follows/{userId}` | 关注用户 | Header: `Authorization`, Path: `userId` | `Void` |
| DELETE | `/api/follows/{userId}` | 取消关注 | Header: `Authorization`, Path: `userId` | `Void` |
| GET | `/api/follows/{userId}/status` | 关注状态 | Header: `Authorization`, Path: `userId` | `Boolean` |
| GET | `/api/follows/my/following` | 我的关注 | Header: `Authorization`, Query: `pageNum`, `pageSize` | `PageResult<FollowUserResponse>` |
| POST | `/api/likes/article/{articleId}` | 点赞文章 | Header: `Authorization`, Path: `articleId` | `Void` |
| DELETE | `/api/likes/article/{articleId}` | 取消点赞 | Header: `Authorization`, Path: `articleId` | `Void` |
| GET | `/api/likes/my` | 我点赞的文章 | Header: `Authorization`, Query: `pageNum`, `pageSize` | `PageResult<ArticleLikeResponse>` |
| GET | `/api/likes/article/{articleId}/status` | 点赞状态 | Header: `Authorization`, Path: `articleId` | `Boolean` |
| GET | `/api/likes/article/{articleId}/count` | 点赞数量 | Path: `articleId` | `Long` |

### 5.6 文件模块 (`/api/files`)

| 方法 | 路径 | 描述 | 请求参数/Body | 响应数据 |
| --- | --- | --- | --- | --- |
| POST | `/upload` | 上传文件 | Header: `Authorization`, Form: `file`, Query: `type` (avatar/cover/content) | Map: `{"url": "...", "type": "..."}` |
| DELETE | `/` | 删除文件 | Header: `Authorization`, Query: `url` | `Boolean` |

## 6. 关键数据模型

### UserResponse
```json
{
  "id": 1,
  "username": "user",
  "nickname": "User",
  "avatar": "http://...",
  "bio": "...",
  "role": "USER",
  "createdAt": "2023-..."
}
```

### ArticleResponse
```json
{
  "id": 1,
  "title": "Title",
  "content": "Markdown Content",
  "coverImage": "http://...",
  "summary": "...",
  "category": { "id": 1, "name": "Tech" },
  "tags": [ { "id": 1, "name": "Java" } ],
  "author": { "id": 1, "nickname": "User", "avatar": "..." },
  "createdAt": "...",
  "updatedAt": "..."
}
```

### CommentResponse (树形)
```json
{
  "id": 1,
  "content": "Comment",
  "user": { ... },
  "children": [
    {
      "id": 2,
      "content": "Reply",
      "user": { ... },
      "replyToUser": { ... }
    }
  ]
}
```

## 7. 前端开发注意事项
1.  **图片上传**: 文章编辑器上传图片时，调用 `/api/files/upload?type=content`，获取 URL 后插入 Markdown。
2.  **Markdown 渲染**: 前端需使用 Markdown 渲染库（如 `react-markdown` 或 `marked`）展示文章内容。
3.  **鉴权处理**: Axios/Fetch 拦截器需自动附加 `Authorization: Bearer ${token}`，并在令牌过期时引导用户重新登录。
4.  **错误处理**: 统一拦截非 200 状态码或 `code != 200` 的响应，并展示 `message`。
