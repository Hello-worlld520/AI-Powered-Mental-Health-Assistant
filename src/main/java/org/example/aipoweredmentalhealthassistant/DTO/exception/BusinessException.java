package org.example.aipoweredmentalhealthassistant.DTO.exception;

import org.example.aipoweredmentalhealthassistant.common.ResultCode;

/**
 * @deprecated 请使用 {@link org.example.aipoweredmentalhealthassistant.exception.BusinessException}。
 */
@Deprecated
public class BusinessException extends org.example.aipoweredmentalhealthassistant.exception.BusinessException {

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }
}
