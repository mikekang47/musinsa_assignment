package io.github.hoo47.musinsa_assignment.application.product.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CategoryProductSummaryResponse(
        List<CategoryProductPriceInfo> items,
        BigDecimal totalPrice
) {


    public record CategoryProductPriceInfo(
            Long categoryId,
            String categoryName,
            Long brandId,
            String brandName,
            BigDecimal price
    ) {

    }
}
