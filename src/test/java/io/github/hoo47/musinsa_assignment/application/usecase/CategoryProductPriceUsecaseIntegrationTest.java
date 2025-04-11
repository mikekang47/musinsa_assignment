package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CategoryProductPriceUsecase 통합 테스트
 * 캐싱 기능 테스트를 위해 첫번째 조회와 두번째 조회의 시간 차이를 확인할 수 있습니다.
 */
@SpringBootTest
@ActiveProfiles("test")
class CategoryProductPriceUsecaseIntegrationTest {

    @Autowired
    private CategoryProductPriceUsecase usecase;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 기존 데이터 삭제
        productRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();

        // 테스트 데이터 준비
        // 카테고리 생성
        Category category1 = Category.builder().name("상의").build();
        Category category2 = Category.builder().name("하의").build();
        Category category3 = Category.builder().name("신발").build();
        categoryRepository.saveAll(List.of(category1, category2, category3));

        // 브랜드 생성
        Brand brandA = Brand.builder().name("브랜드A").build();
        Brand brandB = Brand.builder().name("브랜드B").build();
        Brand brandC = Brand.builder().name("브랜드C").build();
        brandRepository.saveAll(List.of(brandA, brandB, brandC));

        // 각 카테고리별 최저가 상품 생성
        Product product1 = Product.builder()
                .category(category1)
                .brand(brandA)
                .price(new BigDecimal("10000"))
                .build();

        Product product2 = Product.builder()
                .category(category1)
                .brand(brandB)
                .price(new BigDecimal("15000"))
                .build();

        Product product3 = Product.builder()
                .category(category2)
                .brand(brandB)
                .price(new BigDecimal("20000"))
                .build();

        Product product4 = Product.builder()
                .category(category2)
                .brand(brandC)
                .price(new BigDecimal("25000"))
                .build();

        Product product5 = Product.builder()
                .category(category3)
                .brand(brandC)
                .price(new BigDecimal("30000"))
                .build();

        Product product6 = Product.builder()
                .category(category3)
                .brand(brandA)
                .price(new BigDecimal("35000"))
                .build();

        productRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("모든 카테고리의 최저가 상품을 올바르게 조회한다")
    void testGetCategoryPricing() {
        // when
        CategoryProductSummaryResponse response = usecase.getCategoryPricing();

        // then
        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(3); // 3개 카테고리

        // 총액 검증 (10000 + 20000 + 30000 = 60000)
        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("60000"));

        // 카테고리별 검증
        var items = response.items();

        // 상의 카테고리 검증
        var upperItem = items.stream()
                .filter(item -> item.categoryName().equals("상의"))
                .findFirst()
                .orElseThrow();
        assertThat(upperItem.brandName()).isEqualTo("브랜드A");
        assertThat(upperItem.price()).isEqualByComparingTo(new BigDecimal("10000"));

        // 하의 카테고리 검증
        var pantsItem = items.stream()
                .filter(item -> item.categoryName().equals("하의"))
                .findFirst()
                .orElseThrow();
        assertThat(pantsItem.brandName()).isEqualTo("브랜드B");
        assertThat(pantsItem.price()).isEqualByComparingTo(new BigDecimal("20000"));

        // 신발 카테고리 검증
        var shoesItem = items.stream()
                .filter(item -> item.categoryName().equals("신발"))
                .findFirst()
                .orElseThrow();
        assertThat(shoesItem.brandName()).isEqualTo("브랜드C");
        assertThat(shoesItem.price()).isEqualByComparingTo(new BigDecimal("30000"));

        // 캐시 확인을 위한 두번째 호출 - 테스트 환경에서는 캐시가 비활성화되어 있으므로 항상 DB 조회
        CategoryProductSummaryResponse secondResponse = usecase.getCategoryPricing();
        assertThat(secondResponse).isNotNull();
        assertThat(secondResponse.totalPrice()).isEqualByComparingTo(new BigDecimal("60000"));
    }
} 
