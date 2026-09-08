package org.example.aipoweredmentalhealthassistant.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aipoweredmentalhealthassistant.DTO.command.UserloginCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.UserloginResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<UserloginResponseDTO> login(@Valid @RequestBody UserloginCommandDTO commandDTO) {
        // 调用 Service 层的登录方法
        Result<UserloginResponseDTO> result = userService.login(commandDTO);

        // 调试日志
        System.out.println("Login command: " + commandDTO);
        System.out.println("Username: " + commandDTO.getUsername());
        System.out.println("Result: " + result);

        return result;
    }
}