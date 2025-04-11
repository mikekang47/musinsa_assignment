package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryProductPriceUsecaseTest {

    private static final  ProductQueryService productQueryService = mock(ProductQueryService.class);

    private static final CategoryQueryService categoryQueryService = mock(CategoryQueryService.class);

    private static final CategoryProductPriceUsecase categoryProductPriceUsecase = new CategoryProductPriceUsecase(productQueryService, categoryQueryService);

    @Test
    @DisplayName("모든 카테고리의 최저가 상품을 조회하고 총액을 계산한다")
    void getCategoryPricing_ShouldReturnCategoryProductSummary() {
        // given
        Category category1 = createCategory(1L, "상의");
        Category category2 = createCategory(2L, "하의");
        Category category3 = createCategory(3L, "신발");

        Brand brand1 = createBrand(1L, "A");
        Brand brand2 = createBrand(2L, "B");
        Brand brand3 = createBrand(3L, "C");

        Product product1 = createProduct(1L, category1, brand1, new BigDecimal("10000"));
        Product product2 = createProduct(2L, category2, brand2, new BigDecimal("20000"));
        Product product3 = createProduct(3L, category3, brand3, new BigDecimal("30000"));

        List<Category> categories = Arrays.asList(category1, category2, category3);
        List<Product> cheapestProducts = Arrays.asList(product1, product2, product3);

        when(categoryQueryService.getAllCategories()).thenReturn(categories);
        when(productQueryService.getCheapestProductInCategory(anyList())).thenReturn(cheapestProducts);

        // when
        CategoryProductSummaryResponse result = categoryProductPriceUsecase.getCategoryPricing();

        // then
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(3);
        assertThat(result.totalPrice()).isEqualByComparingTo(new BigDecimal("60000"));

        // 카테고리별 최저가 상품 정보 확인
        var productInfos = result.items();

        assertThat(productInfos.get(0).categoryId()).isEqualTo(1L);
        assertThat(productInfos.get(0).categoryName()).isEqualTo("상의");
        assertThat(productInfos.get(0).brandId()).isEqualTo(1L);
        assertThat(productInfos.get(0).brandName()).isEqualTo("A");
        assertThat(productInfos.get(0).price()).isEqualByComparingTo(new BigDecimal("10000"));

        assertThat(productInfos.get(1).categoryId()).isEqualTo(2L);
        assertThat(productInfos.get(1).categoryName()).isEqualTo("하의");
        assertThat(productInfos.get(1).brandId()).isEqualTo(2L);
        assertThat(productInfos.get(1).brandName()).isEqualTo("B");
        assertThat(productInfos.get(1).price()).isEqualByComparingTo(new BigDecimal("20000"));

        assertThat(productInfos.get(2).categoryId()).isEqualTo(3L);
        assertThat(productInfos.get(2).categoryName()).isEqualTo("신발");
        assertThat(productInfos.get(2).brandId()).isEqualTo(3L);
        assertThat(productInfos.get(2).brandName()).isEqualTo("C");
        assertThat(productInfos.get(2).price()).isEqualByComparingTo(new BigDecimal("30000"));
    }

    @Test
    @DisplayName("일부 카테고리에 상품이 없는 경우에도 정상적으로 처리한다")
    void getCategoryPricing_WithMissingProducts_ShouldReturnAvailableProducts() {
        // given
        Category category1 = createCategory(1L, "상의");
        Category category2 = createCategory(2L, "하의");
        Category category3 = createCategory(3L, "신발");

        Brand brand1 = createBrand(1L, "A");
        Brand brand3 = createBrand(3L, "C");

        Product product1 = createProduct(1L, category1, brand1, new BigDecimal("10000"));
        Product product3 = createProduct(3L, category3, brand3, new BigDecimal("30000"));

        List<Category> categories = Arrays.asList(category1, category2, category3);
        List<Product> cheapestProducts = Arrays.asList(product1, null, product3); // 하의 카테고리 상품 없음

        when(categoryQueryService.getAllCategories()).thenReturn(categories);
        when(productQueryService.getCheapestProductInCategory(anyList())).thenReturn(cheapestProducts);

        // when
        CategoryProductSummaryResponse result = categoryProductPriceUsecase.getCategoryPricing();

        // then
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(2); // null 필터링 되었으므로 2개
        assertThat(result.totalPrice()).isEqualByComparingTo(new BigDecimal("40000"));
    }

    @Test
    @DisplayName("카테고리가 없는 경우 빈 리스트와 0원이 반환된다")
    void getCategoryPricing_WithNoCategories_ShouldReturnEmptyResultAndZeroSum() {
        // given
        when(categoryQueryService.getAllCategories()).thenReturn(List.of());
        when(productQueryService.getCheapestProductInCategory(anyList())).thenReturn(List.of());

        // when
        CategoryProductSummaryResponse result = categoryProductPriceUsecase.getCategoryPricing();

        // then
        assertThat(result).isNotNull();
        assertThat(result.items()).isEmpty();
        assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // 테스트용 객체 생성 헬퍼 메서드
    private Category createCategory(Long id, String name) {
        return new Category(id, name);
    }

    private Brand createBrand(Long id, String name) {
        return new Brand(id, name);
    }

    private Product createProduct(Long id, Category category, Brand brand, BigDecimal price) {
        return Product.builder()
                .category(category)
                .brand(brand)
                .price(price)
                .build();
    }
}
