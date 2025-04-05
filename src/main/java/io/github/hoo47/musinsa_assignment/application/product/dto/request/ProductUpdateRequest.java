package io.github.hoo47.musinsa_assignment.application.product.dto.request;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        Long categoryId,
        Long brandId,
        BigDecimal price
) {
} 