package io.github.hoo47.musinsa_assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode {
    // Common
    INVALID_INPUT("COMMON-001", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PATCH_REQUEST("COMMON-002", "잘못된 패치 요청입니다.", HttpStatus.BAD_REQUEST),

    // Category
    CATEGORY_NOT_FOUND("CATEGORY-001", "존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND),

    // Brand
    BRAND_NOT_FOUND("BRAND-001", "존재하지 않는 브랜드입니다.", HttpStatus.NOT_FOUND),
    INVALID_BRAND_NAME("BRAND-002", "브랜드 이름은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Product
    INVALID_PRICE("PRODUCT-001", "상품 가격은 0보다 크거나 같아야 합니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("PRODUCT-002", "존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
} 
