package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    private Category category1;
    private Category category2;
    private Brand brand1;
    private Brand brand2;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;

    @BeforeEach
    void setUp() {
        // 카테고리 세팅
        category1 = categoryRepository.save(Category.builder().name("상의").build());
        category2 = categoryRepository.save(Category.builder().name("하의").build());

        // 브랜드 세팅
        brand1 = brandRepository.save(Brand.builder().name("브랜드A").build());
        brand2 = brandRepository.save(Brand.builder().name("브랜드B").build());

        // 상품 세팅
        product1 = productRepository.save(Product.builder()
                .category(category1)
                .brand(brand1)
                .price(new BigDecimal("10000"))
                .build());

        product2 = productRepository.save(Product.builder()
                .category(category1)
                .brand(brand2)
                .price(new BigDecimal("5000"))  // 브랜드B의 상의 카테고리 최저가
                .build());

        product3 = productRepository.save(Product.builder()
                .category(category2)
                .brand(brand1)
                .price(new BigDecimal("15000"))  // 브랜드A의 하의 카테고리 최저가
                .build());

        product4 = productRepository.save(Product.builder()
                .category(category2)
                .brand(brand2)
                .price(new BigDecimal("20000"))
                .build());
    }

    @AfterEach
    @Transactional
    void tearDown() {
        productRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("모든 상품을 조회할 수 있다")
    void findAllTest() {
        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).isNotEmpty();
        assertThat(products.size()).isGreaterThan(0);

        assertThat(products.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("주어진 카테고리들에 속한 가장 저렴한 상품을 조회할 수 있다")
    void findTop1ByCategoryIdOrderByPriceAscTest() {
        // given
        Category category = categoryRepository.findAll().get(0);

        // when
        List<Product> products = productRepository.findCheapestProductsByCategory(List.of(category.getId()));

        // then
        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(5000));
    }

    @Test
    @DisplayName("브랜드별 카테고리 최저가 상품을 조회한다")
    void findCheapestProductsGroupByBrandAndCategory() {
        // when
        List<BrandCategoryPriceInfo> results = productRepository.findCheapestProductsGroupByBrandAndCategory();

        // then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(4);  // 2개 브랜드 x 2개 카테고리 = 4개 결과

        // 브랜드별 결과 필터링
        List<BrandCategoryPriceInfo> brandAResults = results.stream()
                .filter(info -> info.brandName().equals(brand1.getName()))
                .collect(Collectors.toList());

        List<BrandCategoryPriceInfo> brandBResults = results.stream()
                .filter(info -> info.brandName().equals(brand2.getName()))
                .collect(Collectors.toList());

        // 브랜드A 결과 검증
        assertThat(brandAResults).hasSize(2);

        BrandCategoryPriceInfo brandACategory1 = findInfoByCategory(brandAResults, category1.getName());
        assertThat(brandACategory1.price()).isEqualByComparingTo(new BigDecimal("10000"));

        BrandCategoryPriceInfo brandACategory2 = findInfoByCategory(brandAResults, category2.getName());
        assertThat(brandACategory2.price()).isEqualByComparingTo(new BigDecimal("15000"));

        // 브랜드B 결과 검증
        assertThat(brandBResults).hasSize(2);

        BrandCategoryPriceInfo brandBCategory1 = findInfoByCategory(brandBResults, category1.getName());
        assertThat(brandBCategory1.price()).isEqualByComparingTo(new BigDecimal("5000"));

        BrandCategoryPriceInfo brandBCategory2 = findInfoByCategory(brandBResults, category2.getName());
        assertThat(brandBCategory2.price()).isEqualByComparingTo(new BigDecimal("20000"));
    }

    // 헬퍼 메서드
    private BrandCategoryPriceInfo findInfoByCategory(List<BrandCategoryPriceInfo> infos, String categoryName) {
        return infos.stream()
                .filter(info -> info.categoryName().equals(categoryName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 카테고리 정보를 찾을 수 없습니다: " + categoryName));
    }
} 
