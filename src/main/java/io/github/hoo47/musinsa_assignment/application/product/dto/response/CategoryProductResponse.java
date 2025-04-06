package io.github.hoo47.musinsa_assignment.application.product.dto.response;

import java.math.BigDecimal;

import io.github.hoo47.musinsa_assignment.domain.product.Product;

public record CategoryProductResponse(
        String categoryName,
        String brandName,
        BigDecimal price
) {
    public static CategoryProductResponse from(Product product) {
        return new CategoryProductResponse(
                product.getCategory().getName(),
                product.getBrand().getName(),
                product.getPrice()
        );
    }
} 