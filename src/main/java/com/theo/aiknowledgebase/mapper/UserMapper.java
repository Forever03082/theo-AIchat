package com.theo.aiknowledgebase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.theo.aiknowledgebase.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 移除了手写的 @Select，直接使用 MyBatis-Plus 的标准 CRUD
}