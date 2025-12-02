package com.cuit.blog.common.result;

import lombok.Getter;

// 响应状态码枚举
@Getter
public enum ResultCode {

    // 操作成功
    SUCCESS(200, "操作成功"),

    // 操作失败
    FAILED(500, "操作失败"),

    // 参数校验失败
    VALIDATE_FAILED(400, "参数校验失败"),

    // 请求参数错误
    BAD_REQUEST(400, "请求参数错误"),

    // 未登录或token已过期
    UNAUTHORIZED(401, "未登录或token已过期"),

    // 没有相关权限
    FORBIDDEN(403, "没有相关权限"),

    // 资源不存在
    NOT_FOUND(404, "资源不存在"),

    // 用户名已存在
    USERNAME_EXIST(1001, "用户名已存在"),

    // 邮箱已存在
    EMAIL_EXIST(1002, "邮箱已存在"),

    // 用户不存在
    USER_NOT_FOUND(1003, "用户不存在"),

    // 密码错误
    PASSWORD_ERROR(1004, "密码错误"),

    // 文章不存在
    ARTICLE_NOT_FOUND(2001, "文章不存在"),

    // 分类不存在
    CATEGORY_NOT_FOUND(2002, "分类不存在"),

    // 评论不存在
    COMMENT_NOT_FOUND(3001, "评论不存在"),

    // 文件上传失败
    FILE_UPLOAD_FAILED(4001, "文件上传失败"),

    // 文件类型不支持
    FILE_TYPE_NOT_SUPPORT(4002, "文件类型不支持"),

    // 文件大小超出限制
    FILE_SIZE_EXCEED(4003, "文件大小超出限制"),

    // 已经关注过
    ALREADY_FOLLOWED(6001, "已经关注过该用户"),

    // 未关注
    NOT_FOLLOWED(6002, "未关注该用户"),

    // 不能关注自己
    CANNOT_FOLLOW_SELF(6003, "不能关注自己");

    // 状态码
    private final Integer code;

    // 提示信息
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
