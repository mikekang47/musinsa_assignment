package io.github.hoo47.musinsa_assignment.web.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(response, determineHttpStatus(e.getErrorCode()));
    }

    private HttpStatus determineHttpStatus(BusinessErrorCode errorCode) {
        return switch (errorCode) {
            case CATEGORY_NOT_FOUND, PRODUCT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_INPUT, INVALID_PRICE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        }

        return errors;
    }
} 