package io.github.hoo47.musinsa_assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode {
    // Common
    INVALID_INPUT("COMMON-001", "잘못된 입력값입니다."),

    // Category
    CATEGORY_NOT_FOUND("CATEGORY-001", "존재하지 않는 카테고리입니다."),

    // Brand
    BRAND_NOT_FOUND("BRAND-001", "존재하지 않는 브랜드입니다."),
    INVALID_BRAND_NAME("BRAND-002", "브랜드 이름은 비어있을 수 없습니다."),

    // Product
    INVALID_PRICE("PRODUCT-001", "상품 가격은 0보다 크거나 같아야 합니다."),
    PRODUCT_NOT_FOUND("PRODUCT-002", "존재하지 않는 상품입니다.");

    private final String code;
    private final String message;
} 
