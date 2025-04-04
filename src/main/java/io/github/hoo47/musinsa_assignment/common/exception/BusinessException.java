package io.github.hoo47.musinsa_assignment.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
} 