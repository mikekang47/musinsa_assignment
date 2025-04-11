package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryProductPriceUsecase {

    private final ProductQueryService productQueryService;
    private final CategoryQueryService categoryQueryService;

    /**
     * 모든 카테고리의 최저가 상품과 총액을 계산하여 반환합니다.
     * 결과는 캐시에 저장되어 반복 요청 시 DB 쿼리 없이 빠르게 응답합니다.
     *
     * @return 각 카테고리별 최저가 상품 정보와 총액
     */
    @Cacheable(value = "categoryPricingCache")
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
                            category.getId(),
                            category.getName(),
                            brand.getId(),
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
