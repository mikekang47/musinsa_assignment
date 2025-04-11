package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse.CategoryPrice;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BrandLowestPriceUsecaseIntegrationTest {

    @Autowired
    private BrandLowestPriceUsecase brandLowestPriceUsecase;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    private Category category1;
    private Category category2;
    private Category category3;
    private Brand brandA;
    private Brand brandB;
    private Brand brandC;

    @BeforeEach
    void setUp() {
        clearAllData();
        setupTestData();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        clearAllData();
    }

    private void clearAllData() {
        productRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    private void setupTestData() {
        // 카테고리 생성
        category1 = categoryRepository.save(Category.builder().name("상의").build());
        category2 = categoryRepository.save(Category.builder().name("하의").build());
        category3 = categoryRepository.save(Category.builder().name("신발").build());

        // 브랜드 생성
        brandA = brandRepository.save(Brand.builder().name("브랜드A").build());
        brandB = brandRepository.save(Brand.builder().name("브랜드B").build());
        brandC = brandRepository.save(Brand.builder().name("브랜드C").build());

        // 브랜드A 상품
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandA)
                .price(new BigDecimal("10000"))
                .build());
        productRepository.save(Product.builder()
                .category(category2)
                .brand(brandA)
                .price(new BigDecimal("20000"))
                .build());
        productRepository.save(Product.builder()
                .category(category3)
                .brand(brandA)
                .price(new BigDecimal("30000"))
                .build());

        // 브랜드B 상품
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandB)
                .price(new BigDecimal("5000"))
                .build());
        productRepository.save(Product.builder()
                .category(category2)
                .brand(brandB)
                .price(new BigDecimal("25000"))
                .build());
        productRepository.save(Product.builder()
                .category(category3)
                .brand(brandB)
                .price(new BigDecimal("20000"))
                .build());

        // 브랜드C 상품
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandC)
                .price(new BigDecimal("15000"))
                .build());
        productRepository.save(Product.builder()
                .category(category2)
                .brand(brandC)
                .price(new BigDecimal("15000"))
                .build());
        productRepository.save(Product.builder()
                .category(category3)
                .brand(brandC)
                .price(new BigDecimal("15000"))
                .build());
    }

    @Test
    @DisplayName("단일 브랜드로 모든 카테고리 구매 시 최저가 브랜드와 가격을 조회한다")
    void getBrandWithLowestTotalPrice_ShouldReturnLowestPriceBrand() {
        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNotNull();
        assertThat(response.lowestPrice().brandName()).isEqualTo("브랜드C"); // 브랜드C의 총액: 45000
        assertThat(response.lowestPrice().totalPrice()).isEqualByComparingTo(new BigDecimal("45000"));
        assertThat(response.lowestPrice().categories()).hasSize(3);

        // 카테고리별 가격 검증
        // 상의 카테고리 검증
        assertThat(findCategoryPrice(response, "상의")).isNotNull();
        assertThat(findCategoryPrice(response, "상의").price()).isEqualByComparingTo(new BigDecimal("15000"));

        // 하의 카테고리 검증
        assertThat(findCategoryPrice(response, "하의")).isNotNull();
        assertThat(findCategoryPrice(response, "하의").price()).isEqualByComparingTo(new BigDecimal("15000"));

        // 신발 카테고리 검증
        assertThat(findCategoryPrice(response, "신발")).isNotNull();
        assertThat(findCategoryPrice(response, "신발").price()).isEqualByComparingTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("한 브랜드가 모든 카테고리를 커버하지 못하면 최저가 계산에서 제외된다")
    void getBrandWithLowestTotalPrice_ShouldExcludeBrandsNotCoveringAllCategories() {
        // given
        clearAllData();

        // 카테고리 생성
        category1 = categoryRepository.save(Category.builder().name("상의").build());
        category2 = categoryRepository.save(Category.builder().name("하의").build());
        category3 = categoryRepository.save(Category.builder().name("신발").build());

        // 브랜드 생성
        brandA = brandRepository.save(Brand.builder().name("브랜드A").build());
        brandB = brandRepository.save(Brand.builder().name("브랜드B").build());

        // 브랜드A는 모든 카테고리 커버
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandA)
                .price(new BigDecimal("10000"))
                .build());
        productRepository.save(Product.builder()
                .category(category2)
                .brand(brandA)
                .price(new BigDecimal("20000"))
                .build());
        productRepository.save(Product.builder()
                .category(category3)
                .brand(brandA)
                .price(new BigDecimal("30000"))
                .build());

        // 브랜드B는 일부 카테고리만 커버
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandB)
                .price(new BigDecimal("5000"))
                .build());
        productRepository.save(Product.builder()
                .category(category2)
                .brand(brandB)
                .price(new BigDecimal("5000"))
                .build());
        // 브랜드B는 신발 카테고리 없음

        // when
        BrandProductSummaryResponse response = brandLowestPriceUsecase.getBrandWithLowestTotalPrice();

        // then
        assertThat(response).isNotNull();
        assertThat(response.lowestPrice().brandName()).isEqualTo("브랜드A"); // 브랜드B는 제외되므로 브랜드A만 고려됨
        assertThat(response.lowestPrice().totalPrice()).isEqualByComparingTo(new BigDecimal("60000"));
        assertThat(response.lowestPrice().categories()).hasSize(3);

        // 카테고리별 가격 검증
        assertThat(findCategoryPrice(response, "상의").price()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(findCategoryPrice(response, "하의").price()).isEqualByComparingTo(new BigDecimal("20000"));
        assertThat(findCategoryPrice(response, "신발").price()).isEqualByComparingTo(new BigDecimal("30000"));
    }

    @Test
    @DisplayName("모든 카테고리를 커버하는 브랜드가 없으면 null을 반환한다")
    void getBrandWithLowestTotalPrice_ShouldReturnNullWhenNoBrandCoversAllCategories() {
        // given
        clearAllData();

        // 카테고리 생성
        category1 = categoryRepository.save(Category.builder().name("상의").build());
        category2 = categoryRepository.save(Category.builder().name("하의").build());
        category3 = categoryRepository.save(Category.builder().name("신발").build());

        // 브랜드 생성
        brandA = brandRepository.save(Brand.builder().name("브랜드A").build());
        brandB = brandRepository.save(Brand.builder().name("브랜드B").build());

        // 브랜드A는 일부 카테고리만 커버
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandA)
                .price(new BigDecimal("10000"))
                .build());
        productRepository.save(Product.builder()
                .category(category2)
                .brand(brandA)
                .price(new BigDecimal("20000"))
                .build());
        // 브랜드A는 신발 카테고리 없음

        // 브랜드B도 일부 카테고리만 커버
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brandB)
                .price(new BigDecimal("5000"))
                .build());
        // 브랜드B는 하의, 신발 카테고리 없음

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
