package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.brand.dto.response.CategoryPriceSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryPriceSummaryUsecase {

    private final ProductQueryService productQueryService;

    /**
     * Get price summary (lowest and highest prices) for a specific category
     * This method uses optimized repository queries to retrieve data efficiently.
     * 결과는 캐시에 저장되어 동일한 카테고리 요청 시 DB 쿼리 없이 빠르게 응답합니다.
     *
     * @param categoryName the name of the category to get price summary for
     * @return a CategoryPriceSummaryResponse containing the category name, lowest prices, and highest prices
     */
    @Cacheable(value = "priceSummaryCache", key = "#categoryName")
    public CategoryPriceSummaryResponse getPriceSummaryByCategoryName(String categoryName) {
        // Get all products with the lowest price for the category
        List<Product> cheapestProducts = productQueryService.findCheapestByCategoryName(categoryName);
        // Get all products with the highest price for the category
        List<Product> expensiveProducts = productQueryService.findMostExpensiveByCategoryName(categoryName);

        List<CategoryPriceSummaryResponse.PriceInfo> lowestPrices = cheapestProducts.stream()
                .map(p -> new CategoryPriceSummaryResponse.PriceInfo(p.getBrand().getName(), p.getPrice()))
                .toList();

        List<CategoryPriceSummaryResponse.PriceInfo> highestPrices = expensiveProducts.stream()
                .map(p -> new CategoryPriceSummaryResponse.PriceInfo(p.getBrand().getName(), p.getPrice()))
                .toList();

        return new CategoryPriceSummaryResponse(categoryName, lowestPrices, highestPrices);
    }
}
