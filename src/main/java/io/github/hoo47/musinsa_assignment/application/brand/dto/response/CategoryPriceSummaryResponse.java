package io.github.hoo47.musinsa_assignment.application.brand.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CategoryPriceSummaryResponse(
        String category,
        List<PriceInfo> lowestPrice,
        List<PriceInfo> highestPrice
) {
    public record PriceInfo(
            String brand,
            BigDecimal price
    ) {
    }
}
