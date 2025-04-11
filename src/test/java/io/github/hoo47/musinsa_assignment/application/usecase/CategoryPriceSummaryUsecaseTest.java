package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.brand.dto.response.CategoryPriceSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryPriceSummaryUsecaseTest {

    private static final ProductQueryService productQueryService = Mockito.mock(ProductQueryService.class);
    private static final CategoryPriceSummaryUsecase usecase = new CategoryPriceSummaryUsecase(productQueryService);

    private static final String categoryName = "상의";


    @Test
    @DisplayName("usecase returns proper summary response for a valid category")
    void testGetPriceSummaryByCategoryName() {

        // Prepare lowest price products (simulate 2 products with same low price for BrandA)
        Product p1 = Product.builder().price(BigDecimal.valueOf(10000))
                .brand(Brand.builder().name("BrandA").build()).build();
        Product p2 = Product.builder().price(BigDecimal.valueOf(10000))
                .brand(Brand.builder().name("BrandA").build()).build();

        // Prepare highest price product for BrandB
        Product p3 = Product.builder().price(BigDecimal.valueOf(30000))
                .brand(Brand.builder().name("BrandB").build()).build();

        Mockito.when(productQueryService.findCheapestByCategoryName(categoryName))
                .thenReturn(List.of(p1, p2));
        Mockito.when(productQueryService.findMostExpensiveByCategoryName(categoryName))
                .thenReturn(List.of(p3));

        CategoryPriceSummaryResponse response = usecase.getPriceSummaryByCategoryName(categoryName);
        assertThat(response).isNotNull();
        assertThat(response.category()).isEqualTo(categoryName);
        assertThat(response.lowestPrice()).hasSize(2);
        assertThat(response.highestPrice()).hasSize(1);
    }
}
