package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BrandLowestPriceUsecase {

    private final CategoryQueryService categoryQueryService;
    private final ProductQueryService productQueryService;

    /**
     * Find the brand with the lowest total price across all categories.
     * This method is optimized to use efficient data structures and stream operations.
     *
     * @return a summary of the brand with the lowest total price
     */
    public BrandProductSummaryResponse getBrandWithLowestTotalPrice() {
        // 1. Get minimum price products by brand and category
        List<BrandCategoryPriceInfo> results = productQueryService.findCheapestProductsGroupByBrandAndCategory();

        if (results.isEmpty()) {
            return null;
        }

        // 2. Get total number of categories
        long totalCategories = categoryQueryService.getCategoryCount();

        // 3. Group by brand and collect category prices
        Map<Long, List<BrandCategoryPriceInfo>> brandCategoryMap = results.stream()
                .collect(Collectors.groupingBy(BrandCategoryPriceInfo::brandId));

        // 4. Find brands that have all categories and calculate total prices
        record BrandSummary(Long brandId, String brandName, BigDecimal totalPrice,
                            List<BrandCategoryPriceInfo> categoryPrices) {
        }

        // Using a stream to find the brand with lowest price
        return brandCategoryMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() == totalCategories) // Only brands with all categories
                .map(entry -> {
                    List<BrandCategoryPriceInfo> categoryPrices = entry.getValue();
                    String brandName = categoryPrices.get(0).brandName(); // All items have same brand name
                    BigDecimal totalPrice = categoryPrices.stream()
                            .map(BrandCategoryPriceInfo::price)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new BrandSummary(entry.getKey(), brandName, totalPrice, categoryPrices);
                })
                .min(Comparator.comparing(BrandSummary::totalPrice)
                        .thenComparing(BrandSummary::brandName))
                .map(summary -> {
                    // Transform to response format
                    List<BrandProductSummaryResponse.CategoryPrice> prices = summary.categoryPrices().stream()
                            .map(info -> new BrandProductSummaryResponse.CategoryPrice(
                                    info.categoryName(), info.price()))
                            .sorted(Comparator.comparing(BrandProductSummaryResponse.CategoryPrice::categoryName))
                            .toList();

                    return BrandProductSummaryResponse.of(
                            summary.brandName(),
                            prices,
                            summary.totalPrice());
                })
                .orElse(null);
    }
}
