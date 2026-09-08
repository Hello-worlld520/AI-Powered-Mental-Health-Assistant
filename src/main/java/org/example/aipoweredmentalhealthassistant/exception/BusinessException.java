package org.example.aipoweredmentalhealthassistant.exception;

import lombok.Getter;
import org.example.aipoweredmentalhealthassistant.common.ResultCode;

@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
