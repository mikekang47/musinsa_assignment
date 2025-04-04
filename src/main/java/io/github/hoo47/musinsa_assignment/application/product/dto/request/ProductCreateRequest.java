package io.github.hoo47.musinsa_assignment.application.product.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotNull(message = "브랜드 ID는 필수입니다")
        Long brandId,

        @NotNull(message = "카테고리 ID는 필수입니다")
        Long categoryId,

        @NotNull(message = "가격은 필수입니다")
        BigDecimal price
) {
}
