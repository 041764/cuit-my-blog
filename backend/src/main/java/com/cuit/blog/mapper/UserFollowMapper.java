package com.cuit.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.blog.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

// 用户关注 Mapper 接口
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
}
