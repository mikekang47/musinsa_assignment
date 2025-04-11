package io.github.hoo47.musinsa_assignment.web.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(BusinessErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }

    public static ErrorResponse of(BusinessErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getCode(),
                message,
                LocalDateTime.now()
        );
    }
} 
