package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.brand.dto.response.CategoryPriceSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryPriceSummaryUsecase {

    private final ProductQueryService productQueryService;

    public CategoryPriceSummaryResponse getPriceSummaryByCategoryName(String categoryName) {
        // Get all products with the lowest price for the category
        List<Product> cheapestProducts = productQueryService.findCheapestByCategoryName(categoryName);
        // Get all products with the highest price for the category
        List<Product> expensiveProducts = productQueryService.findMostExpensiveByCategoryName(categoryName);

        List<CategoryPriceSummaryResponse.PriceInfo> lowestPrices = cheapestProducts.stream()
                .map(p -> new CategoryPriceSummaryResponse.PriceInfo(p.getBrand().getName(), p.getPrice()))
                .collect(Collectors.toList());

        List<CategoryPriceSummaryResponse.PriceInfo> highestPrices = expensiveProducts.stream()
                .map(p -> new CategoryPriceSummaryResponse.PriceInfo(p.getBrand().getName(), p.getPrice()))
                .collect(Collectors.toList());

        return new CategoryPriceSummaryResponse(categoryName, lowestPrices, highestPrices);
    }
}
