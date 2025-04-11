package io.github.hoo47.musinsa_assignment.controller.v1.product.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        BigDecimal price,
        Brand brand,
        Category category
) {

    public static ProductResponse of(Long id, BigDecimal price, Brand brand, Category category) {
        return new ProductResponse(id, price, brand, category);
    }
    public record Brand(
            Long id,
            String name
    ) {
    }

    public record Category(
            Long id,
            String name
    ) {
    }
}
