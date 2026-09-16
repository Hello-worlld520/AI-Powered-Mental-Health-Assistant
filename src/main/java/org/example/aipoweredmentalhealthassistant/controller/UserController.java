package org.example.aipoweredmentalhealthassistant.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aipoweredmentalhealthassistant.DTO.command.UserloginCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.UserloginResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/current")
    public Result<UserloginResponseDTO.UserDetailResponseDTO> current(Authentication authentication) {
        return userService.current(authentication.getName());
    }

    @PostMapping("/login")
    public Result<UserloginResponseDTO> login(@Valid @RequestBody UserloginCommandDTO commandDTO) {
        return userService.login(commandDTO);
    }
}