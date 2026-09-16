package org.example.aipoweredmentalhealthassistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aipoweredmentalhealthassistant.DTO.command.UserloginCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.UserloginResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.common.ResultCode;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.enumClass.UserStatus;
import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.example.aipoweredmentalhealthassistant.mapper.UserMapper;
import org.example.aipoweredmentalhealthassistant.util.JwtUtil;
import org.example.aipoweredmentalhealthassistant.util.UserConvert;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private JwtUtil jwtUtil;

    public Result<UserloginResponseDTO.UserDetailResponseDTO> current(String username) {
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (!Objects.equals(user.getStatus(), UserStatus.NORMAL.getCode())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        return Result.success(UserConvert.toUserDetailResponse(user));
    }

    public Result<UserloginResponseDTO> login(UserloginCommandDTO commandDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, commandDTO.getUsername())
                .or()
                .eq(User::getEmail, commandDTO.getUsername());

        User user = baseMapper.selectOne(queryWrapper);
        if (user == null || !passwordMatches(commandDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        if (!Objects.equals(user.getStatus(), UserStatus.NORMAL.getCode())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        UserloginResponseDTO response = new UserloginResponseDTO();
        response.setToken(jwtUtil.generateToken(user.getUsername()));
        response.setUserInfo(UserConvert.toUserDetailResponse(user));
        return Result.success(response);
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(rawPassword, storedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return MessageDigest.isEqual(
                rawPassword.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }
}
