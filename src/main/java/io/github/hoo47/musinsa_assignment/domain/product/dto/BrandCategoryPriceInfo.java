package io.github.hoo47.musinsa_assignment.domain.product.dto;

import java.math.BigDecimal;

public record BrandCategoryPriceInfo(
    Long brandId, 
    String brandName, 
    Long categoryId, 
    String categoryName, 
    BigDecimal price
) {} 