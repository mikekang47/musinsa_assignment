package io.github.hoo47.musinsa_assignment.application.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryProductPriceUsecase {

    private final ProductQueryService productQueryService;
    private final CategoryQueryService categoryQueryService;

    public CategoryProductSummaryResponse getCategoryPricing() {
        List<Category> categories = categoryQueryService.getAllCategories();

        List<Product> products = productQueryService.getCheapestProductInCategory(
                categories.stream()
                        .map(Category::getId)
                        .toList()
        );

        var categoryProductInfos = products.stream()
                .filter(Objects::nonNull)
                .map(product -> {
                    Category category = product.getCategory();
                    Brand brand = product.getBrand();
                    BigDecimal price = product.getPrice();
                    return new CategoryProductSummaryResponse.CategoryProductPriceInfo(
                            category.getName(),
                            brand.getName(),
                            price
                    );
                })
                .toList();

        BigDecimal total = categoryProductInfos.stream()
                .map(CategoryProductSummaryResponse.CategoryProductPriceInfo::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CategoryProductSummaryResponse(
                categoryProductInfos,
                total
        );

    }
}
