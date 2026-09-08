package org.example.aipoweredmentalhealthassistant.common;

import lombok.Data;

@Data
public class Result <T> {
    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return of(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> failure(ResultCode resultCode) {
        return failure(resultCode, null);
    }

    public static <T> Result<T> failure(ResultCode resultCode, T data) {
        return of(resultCode, data);
    }

    private static <T> Result<T> of(ResultCode resultCode, T data) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMessage());
        result.setData(data);
        return result;
    }
}