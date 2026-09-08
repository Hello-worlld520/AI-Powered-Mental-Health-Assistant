package org.example.aipoweredmentalhealthassistant.common;

import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;   // ← FieldError 在这里
import java.util.stream.Collectors;                 // ← Collectors 在这里
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.failure(e.getResultCode());
    }

    // 处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handlerException(MethodArgumentNotValidException e) {
        // 处理异常数据的处理：获取所有校验失败的信息，用逗号拼接成字符串
        String message = e.getBindingResult()          // 获取校验结果对象
                .getFieldErrors()            // 获取所有字段错误列表（List<FieldError>）
                .stream()                    // 转换成流
                .map(FieldError::getDefaultMessage) // 提取每条错误的 message
                .collect(Collectors.joining(",")); // 用逗号拼接成字符串
        return Result.failure(ResultCode.BAD_REQUEST, message);
    }
}
