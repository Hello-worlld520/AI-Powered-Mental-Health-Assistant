package org.example.aipoweredmentalhealthassistant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.example.aipoweredmentalhealthassistant.common.Result;

@RestController
@RequestMapping("/api")
public class Test {
    @GetMapping("/test")
    public Result<?> test() {
        return Result.success();
    }
}