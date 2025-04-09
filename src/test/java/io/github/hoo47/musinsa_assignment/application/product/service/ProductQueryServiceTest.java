package io.github.hoo47.musinsa_assignment.application.product.service;

import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductQueryServiceTest {

    @Autowired
    private ProductQueryService productQueryService;

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

    @BeforeEach
    @Transactional
    void setUp() {
        // 카테고리 생성
        category1 = categoryRepository.save(Category.builder()
                .name("상의")
                .build());

        category2 = categoryRepository.save(Category.builder()
                .name("아우터")
                .build());

        // 브랜드 생성
        brand1 = brandRepository.save(Brand.builder()
                .name("A")
                .build());

        brand2 = brandRepository.save(Brand.builder()
                .name("B")
                .build());

        // 상품 생성
        productRepository.save(Product.builder()
                .category(category1)
                .brand(brand1)
                .price(BigDecimal.valueOf(10000))
                .build());

        productRepository.save(Product.builder()
                .category(category2)
                .brand(brand1)
                .price(BigDecimal.valueOf(30000))
                .build());

        productRepository.save(Product.builder()
                .category(category1)
                .brand(brand2)
                .price(BigDecimal.valueOf(40000))
                .build());
    }

    @AfterEach
    @Transactional
    void tearDown() {
        productRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("모든 카테고리에서 가장 저렴한 상품들을 조회할 수 있다")
    void findAllCategoryProducts() {
        List<Product> products = productQueryService.getCheapestProductInCategory(
                List.of(category1.getId(), category2.getId())
        );

        Product product = products.get(0);

        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(2);
        assertThat(product.getCategory().getId()).isEqualTo(category1.getId());
        assertThat(product.getBrand().getId()).isEqualTo(brand1.getId());
        assertThat(product.getPrice().compareTo(BigDecimal.valueOf(10000))).isZero();

        Product product1 = products.get(1);
        assertThat(product1.getPrice().compareTo(BigDecimal.valueOf(30000))).isZero();
    }

    @Test
    @DisplayName("성공 케이스: 브랜드별 최저가 상품이 정상적으로 조회된다")
    void testFindCheapestProductsGroupByBrandAndCategory_Success() {
        List<BrandCategoryPriceInfo> infos = productQueryService.findCheapestProductsGroupByBrandAndCategory();

        // Brand A의 경우 두 카테고리 모두 존재
        // Brand B는 한 카테고리 정보만 있음.
        assertThat(infos).isNotEmpty();
        // 전체 결과엔 두 브랜드의 정보가 조회된다.
        // 각각의 BrandCategoryPriceInfo는 브랜드, 카테고리, 최저가 정보를 담음
        long brandACount = infos.stream()
                .filter(info -> info.brandId().equals(brand1.getId()))
                .count();
        long brandBCount = infos.stream()
                .filter(info -> info.brandId().equals(brand2.getId()))
                .count();

        assertThat(brandACount).isEqualTo(2);
        assertThat(brandBCount).isEqualTo(1);
    }

    @Test
    @DisplayName("빈 리스트 응답 케이스: 상품 데이터가 없으면 빈 리스트를 반환한다")
    void testFindCheapestProductsGroupByBrandAndCategory_Empty() {
        // 모든 상품 삭제로 빈 리스트 상황을 구성
        productRepository.deleteAllInBatch();

        List<BrandCategoryPriceInfo> infos = productQueryService.findCheapestProductsGroupByBrandAndCategory();
        assertThat(infos).isEmpty();
    }
}
