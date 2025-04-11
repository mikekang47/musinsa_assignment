package io.github.hoo47.musinsa_assignment.controller.v1.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.hoo47.musinsa_assignment.application.brand.dto.response.CategoryPriceSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.usecase.BrandLowestPriceUsecase;
import io.github.hoo47.musinsa_assignment.application.usecase.CategoryPriceSummaryUsecase;
import io.github.hoo47.musinsa_assignment.application.usecase.CategoryProductPriceUsecase;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryProductPriceUsecase categoryProductPriceUsecase;
    private final BrandLowestPriceUsecase brandLowestPriceUsecase;
    private final CategoryPriceSummaryUsecase categoryPriceSummaryUsecase;

    @GetMapping("/lowest-price-by-category")
    public CategoryProductSummaryResponse getCategoryPricing() {
        return categoryProductPriceUsecase.getCategoryPricing();
    }

    @GetMapping("/{categoryName}/price-summary")
    public CategoryPriceSummaryResponse getCategoryPriceSummary(@PathVariable String categoryName) {
        return categoryPriceSummaryUsecase.getPriceSummaryByCategoryName(categoryName);
    }
}
