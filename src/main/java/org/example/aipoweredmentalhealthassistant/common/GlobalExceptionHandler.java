package org.example.aipoweredmentalhealthassistant.common;

import jakarta.validation.ConstraintViolationException;
import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.failure(e.getResultCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Result.failure(ResultCode.BAD_REQUEST, getFieldErrorMessage(e));
    }

    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        return Result.failure(ResultCode.BAD_REQUEST, getFieldErrorMessage(e));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(","));
        return Result.failure(ResultCode.BAD_REQUEST,
                message.isBlank() ? "请求参数不合法" : message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return Result.failure(ResultCode.BAD_REQUEST, "请求体格式错误");
    }

    private String getFieldErrorMessage(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(","));
        return message.isBlank() ? "请求参数不合法" : message;
    }
}
