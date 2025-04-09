package io.github.hoo47.musinsa_assignment.application.usecase;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse.CategoryPrice;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;

class BrandLowestPriceUsecaseTest {

    private static final ProductQueryService productQueryService = mock(ProductQueryService.class);
    private static final CategoryQueryService categoryQueryService = mock(CategoryQueryService.class);

    private static final BrandLowestPriceUsecase brandLowestPriceUsecase = new BrandLowestPriceUsecase(categoryQueryService, productQueryService);

    @BeforeEach
    void setUp() {
        given(categoryQueryService.getCategoryCount()).willReturn(3L); // 카테고리 수: 3개 (상의, 하의, 신발)
    }

    @Test
    @DisplayName("단일 브랜드로 모든 카테고리 구매 시 최저가 브랜드와 가격을 조회한다")
    void getBrandWithLowestTotalPrice_ShouldReturnLowestPriceBrand() {
        // given
        BrandCategoryPriceInfo info1 = new BrandCategoryPriceInfo(1L, "브랜드A", 1L, "상의", new BigDecimal("10000"));
        BrandCategoryPriceInfo info2 = new BrandCategoryPriceInfo(1L, "브랜드A", 2L, "하의", new BigDecimal("20000"));
        BrandCategoryPriceInfo info3 = new BrandCategoryPriceInfo(1L, "브랜드A", 3L, "신발", new BigDecimal("30000"));
        
        BrandCategoryPriceInfo info4 = new BrandCategoryPriceInfo(2L, "브랜드B", 1L, "상의", new BigDecimal("5000"));
        BrandCategoryPriceInfo info5 = new BrandCategoryPriceInfo(2L, "브랜드B", 2L, "하의", new BigDecimal("25000"));
        BrandCategoryPriceInfo info6 = new BrandCategoryPriceInfo(2L, "브랜드B", 3L, "신발", new BigDecimal("20000"));
        
        BrandCategoryPriceInfo info7 = new BrandCategoryPriceInfo(3L, "브랜드C", 1L, "상의", new BigDecimal("15000"));
        BrandCategoryPriceInfo info8 = new BrandCategoryPriceInfo(3L, "브랜드C", 2L, "하의", new BigDecimal("15000"));
        BrandCategoryPriceInfo info9 = new BrandCategoryPriceInfo(3L, "브랜드C", 3L, "신발", new BigDecimal("15000"));

        List<BrandCategoryPriceInfo> results = Arrays.asList(
                info1, info2, info3, info4, info5, info6, info7, info8, info9
        );

        given(productQueryService.findCheapestProductsGroupByBrandAndCategory()).willReturn(results);

        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNotNull();
        assertThat(response.lowestPrice().brandName()).isEqualTo("브랜드C"); // 브랜드C의 총액: 45000
        assertThat(response.lowestPrice().totalPrice()).isEqualByComparingTo(new BigDecimal("45000"));
        assertThat(response.lowestPrice().categories()).hasSize(3);
        
        // 각 카테고리 가격 검증
        assertThat(findCategoryPrice(response, "상의").price()).isEqualByComparingTo(new BigDecimal("15000"));
        assertThat(findCategoryPrice(response, "하의").price()).isEqualByComparingTo(new BigDecimal("15000"));
        assertThat(findCategoryPrice(response, "신발").price()).isEqualByComparingTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("동일한 가격일 경우 브랜드명 알파벳 순으로 정렬된다")
    void getBrandWithLowestTotalPrice_ShouldOrderByBrandNameWhenSamePrice() {
        // given
        BrandCategoryPriceInfo info1 = new BrandCategoryPriceInfo(1L, "BrandA", 1L, "상의", new BigDecimal("10000"));
        BrandCategoryPriceInfo info2 = new BrandCategoryPriceInfo(1L, "BrandA", 2L, "하의", new BigDecimal("20000"));
        BrandCategoryPriceInfo info3 = new BrandCategoryPriceInfo(1L, "BrandA", 3L, "신발", new BigDecimal("15000"));
        
        BrandCategoryPriceInfo info4 = new BrandCategoryPriceInfo(2L, "BrandB", 1L, "상의", new BigDecimal("15000"));
        BrandCategoryPriceInfo info5 = new BrandCategoryPriceInfo(2L, "BrandB", 2L, "하의", new BigDecimal("15000"));
        BrandCategoryPriceInfo info6 = new BrandCategoryPriceInfo(2L, "BrandB", 3L, "신발", new BigDecimal("15000"));

        List<BrandCategoryPriceInfo> results = Arrays.asList(
                info1, info2, info3, info4, info5, info6
        );

        given(productQueryService.findCheapestProductsGroupByBrandAndCategory()).willReturn(results);
        given(categoryQueryService.getCategoryCount()).willReturn(3L); // 카테고리 수: 3개 (상의, 하의, 신발)

        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNotNull();
        assertThat(response.lowestPrice().brandName()).isEqualTo("BrandA"); // 동일한 가격(45000)이지만 BrandA가 알파벳 순으로 앞섬
        assertThat(response.lowestPrice().totalPrice()).isEqualByComparingTo(new BigDecimal("45000"));
    }

    @Test
    @DisplayName("한 브랜드가 모든 카테고리를 커버하지 못하면 최저가 계산에서 제외된다")
    void getBrandWithLowestTotalPrice_ShouldExcludeBrandsNotCoveringAllCategories() {
        // given
        BrandCategoryPriceInfo info1 = new BrandCategoryPriceInfo(1L, "브랜드A", 1L, "상의", new BigDecimal("10000"));
        BrandCategoryPriceInfo info2 = new BrandCategoryPriceInfo(1L, "브랜드A", 2L, "하의", new BigDecimal("20000"));
        BrandCategoryPriceInfo info3 = new BrandCategoryPriceInfo(1L, "브랜드A", 3L, "신발", new BigDecimal("30000"));
        
        BrandCategoryPriceInfo info4 = new BrandCategoryPriceInfo(2L, "브랜드B", 1L, "상의", new BigDecimal("5000"));
        BrandCategoryPriceInfo info5 = new BrandCategoryPriceInfo(2L, "브랜드B", 2L, "하의", new BigDecimal("5000"));
        // 브랜드B는 신발 카테고리 상품이 없음
        
        List<BrandCategoryPriceInfo> results = Arrays.asList(
                info1, info2, info3, info4, info5
        );

        given(productQueryService.findCheapestProductsGroupByBrandAndCategory()).willReturn(results); 

        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNotNull();
        assertThat(response.lowestPrice().brandName()).isEqualTo("브랜드A"); // 브랜드B는 카테고리를 모두 커버하지 못해 제외됨
        assertThat(response.lowestPrice().totalPrice()).isEqualByComparingTo(new BigDecimal("60000"));
    }

    @Test
    @DisplayName("모든 카테고리를 커버하는 브랜드가 없으면 null을 반환한다")
    void getBrandWithLowestTotalPrice_ShouldReturnNullWhenNoBrandCoversAllCategories() {
        // given
        BrandCategoryPriceInfo info1 = new BrandCategoryPriceInfo(1L, "브랜드A", 1L, "상의", new BigDecimal("10000"));
        BrandCategoryPriceInfo info2 = new BrandCategoryPriceInfo(1L, "브랜드A", 2L, "하의", new BigDecimal("20000"));
        // 브랜드A는 신발 카테고리 상품이 없음
        
        BrandCategoryPriceInfo info3 = new BrandCategoryPriceInfo(2L, "브랜드B", 1L, "상의", new BigDecimal("5000"));
        // 브랜드B는 하의, 신발 카테고리 상품이 없음
        
        BrandCategoryPriceInfo info4 = new BrandCategoryPriceInfo(3L, "브랜드C", 3L, "신발", new BigDecimal("15000"));
        // 브랜드C는 상의, 하의 카테고리 상품이 없음

        List<BrandCategoryPriceInfo> results = Arrays.asList(
                info1, info2, info3, info4
        );

        given(productQueryService.findCheapestProductsGroupByBrandAndCategory()).willReturn(results);

        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("상품 정보가 없으면 null을 반환한다")
    void getBrandWithLowestTotalPrice_ShouldReturnNullWhenNoProducts() {
        // given
        given(productQueryService.findCheapestProductsGroupByBrandAndCategory()).willReturn(Collections.emptyList());

        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNull();
    }
    
    // 카테고리명으로 카테고리 가격 정보를 찾는 헬퍼 메서드
    private CategoryPrice findCategoryPrice(BrandProductSummaryResponse response, String categoryName) {
        return response.lowestPrice().categories().stream()
                .filter(cp -> cp.categoryName().equals(categoryName))
                .findFirst()
                .orElse(null);
    }
}
