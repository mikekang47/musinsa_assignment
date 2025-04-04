package io.github.hoo47.musinsa_assignment.application.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateRequest(
    @NotBlank(message = "브랜드명은 필수입니다")
    String brand,

    @NotNull(message = "카테고리 ID는 필수입니다")
    @Positive(message = "카테고리 ID는 0보다 커야 합니다")
    Long categoryId,

    @NotNull(message = "가격은 필수입니다")
    BigDecimal price
) {} 
