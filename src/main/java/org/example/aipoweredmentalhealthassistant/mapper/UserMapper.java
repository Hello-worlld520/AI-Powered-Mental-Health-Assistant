package org.example.aipoweredmentalhealthassistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.aipoweredmentalhealthassistant.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
