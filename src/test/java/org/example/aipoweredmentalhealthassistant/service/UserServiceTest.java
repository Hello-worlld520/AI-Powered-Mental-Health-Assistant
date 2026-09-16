package org.example.aipoweredmentalhealthassistant.service;

import org.example.aipoweredmentalhealthassistant.DTO.command.UserloginCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.UserloginResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.example.aipoweredmentalhealthassistant.mapper.UserMapper;
import org.example.aipoweredmentalhealthassistant.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
        ReflectionTestUtils.setField(userService, "jwtUtil", jwtUtil);
    }

    @Test
    void loginAcceptsBcryptPassword() {
        User user = activeUser(BCrypt.hashpw("password123", BCrypt.gensalt()));
        when(userMapper.selectOne(any())).thenReturn(user);
        when(jwtUtil.generateToken("test")).thenReturn("token");

        Result<UserloginResponseDTO> result = userService.login(command("password123"));

        assertEquals("200", result.getCode());
        assertEquals("token", result.getData().getToken());
    }

    @Test
    void loginAcceptsLegacyPlaintextPassword() {
        User user = activeUser("password123");
        when(userMapper.selectOne(any())).thenReturn(user);
        when(jwtUtil.generateToken("test")).thenReturn("token");

        Result<UserloginResponseDTO> result = userService.login(command("password123"));

        assertEquals("200", result.getCode());
    }

    @Test
    void loginRejectsIncorrectBcryptPassword() {
        User user = activeUser(BCrypt.hashpw("password123", BCrypt.gensalt()));
        when(userMapper.selectOne(any())).thenReturn(user);

        assertThrows(BusinessException.class, () -> userService.login(command("wrong-password")));
    }

    private User activeUser(String password) {
        User user = new User();
        user.setUsername("test");
        user.setPassword(password);
        user.setStatus(1);
        return user;
    }

    private UserloginCommandDTO command(String password) {
        UserloginCommandDTO command = new UserloginCommandDTO();
        command.setUsername("test");
        command.setPassword(password);
        return command;
    }
}
