package com.cuit.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// 用户关注实体类
@Data
@TableName("user_follow")
public class UserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 关注者ID
    private Long followerId;

    // 被关注者ID
    private Long followingId;

    // 关注时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
