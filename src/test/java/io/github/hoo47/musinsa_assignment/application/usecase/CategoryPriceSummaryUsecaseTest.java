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
import static org.mockito.Mockito.when;

class CategoryPriceSummaryUsecaseTest {

    private static final ProductQueryService productQueryService = Mockito.mock(ProductQueryService.class);
    private static final CategoryPriceSummaryUsecase usecase = new CategoryPriceSummaryUsecase(productQueryService);

    private static final String CATEGORY_NAME = "상의";

    @Test
    @DisplayName("카테고리별 최저가와 최고가 상품 정보를 올바르게 조회한다")
    void testGetPriceSummaryByCategoryName() {
        // given
        // 최저가 상품 준비 (같은 브랜드의 동일 가격 상품 2개)
        Product lowestProduct1 = Product.builder()
                .price(BigDecimal.valueOf(10000))
                .brand(Brand.builder().name("BrandA").build())
                .build();
        Product lowestProduct2 = Product.builder()
                .price(BigDecimal.valueOf(10000))
                .brand(Brand.builder().name("BrandA").build())
                .build();

        // 최고가 상품 준비
        Product highestProduct = Product.builder()
                .price(BigDecimal.valueOf(30000))
                .brand(Brand.builder().name("BrandB").build())
                .build();

        // 서비스 모의 설정
        when(productQueryService.findCheapestByCategoryName(CATEGORY_NAME))
                .thenReturn(List.of(lowestProduct1, lowestProduct2));
        when(productQueryService.findMostExpensiveByCategoryName(CATEGORY_NAME))
                .thenReturn(List.of(highestProduct));

        // when
        CategoryPriceSummaryResponse response = usecase.getPriceSummaryByCategoryName(CATEGORY_NAME);

        // then
        assertThat(response).isNotNull();
        assertThat(response.category()).isEqualTo(CATEGORY_NAME);
        assertThat(response.lowestPrice()).hasSize(2);
        assertThat(response.highestPrice()).hasSize(1);

        // 최저가 정보 검증
        assertThat(response.lowestPrice().get(0).brand()).isEqualTo("BrandA");
        assertThat(response.lowestPrice().get(0).price()).isEqualByComparingTo(BigDecimal.valueOf(10000));

        // 최고가 정보 검증
        assertThat(response.highestPrice().get(0).brand()).isEqualTo("BrandB");
        assertThat(response.highestPrice().get(0).price()).isEqualByComparingTo(BigDecimal.valueOf(30000));
    }
}
