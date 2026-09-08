package org.example.aipoweredmentalhealthassistant.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS("200", "succes"),
    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未登录或认证失败"),
    FORBIDDEN("403", "没有访问权限"),
    NOT_FOUND("404", "资源不存在"),
    INTERNAL_ERROR("500", "服务器内部错误");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
