package io.github.hoo47.musinsa_assignment.application.product.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record BrandProductSummaryResponse(
        LowestPriceInfo lowestPrice
) {
    public record LowestPriceInfo(
            String brandName,
            List<CategoryPrice> categories,
            BigDecimal totalPrice
    ) {
    }

    public record CategoryPrice(
            String categoryName,
            BigDecimal price
    ) {
    }

    public static BrandProductSummaryResponse of(String brandName, List<CategoryPrice> categoryPrices, BigDecimal totalPrice) {
        return new BrandProductSummaryResponse(
                new LowestPriceInfo(brandName, categoryPrices, totalPrice)
        );
    }
} 
