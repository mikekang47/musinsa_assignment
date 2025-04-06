package io.github.hoo47.musinsa_assignment.application.product.dto.response;

import io.github.hoo47.musinsa_assignment.domain.product.Product;

import java.math.BigDecimal;

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
