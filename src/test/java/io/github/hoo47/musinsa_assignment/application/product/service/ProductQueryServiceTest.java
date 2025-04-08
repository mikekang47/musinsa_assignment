package io.github.hoo47.musinsa_assignment.application.product.service;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;

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
        deleteAllData();
        
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
                .category(category1)
                .brand(brand2)
                .price(BigDecimal.valueOf(20000))
                .build());

        productRepository.save(Product.builder()
                .category(category2)
                .brand(brand1)
                .price(BigDecimal.valueOf(30000))
                .build());

        productRepository.save(Product.builder()
                .category(category2)
                .brand(brand2)
                .price(BigDecimal.valueOf(40000))
                .build());
    }

    private void deleteAllData() {
        productRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("모든 카테고리에서 가장 저렴한 상품들을 조회할 수 있다")
    void findAllCategoryProducts() {
        List<Product> products = productQueryService.getCheapestProductInCategory(List.of(category1.getId(), category2.getId()));

        Product product = products.get(0);

        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(2);
        assertThat(product.getCategory().getId()).isEqualTo(category1.getId());
        assertThat(product.getBrand().getId()).isEqualTo(brand1.getId());
        assertThat(product.getPrice().compareTo(BigDecimal.valueOf(10000))).isZero();

        Product product1 = products.get(1);
        assertThat(product1.getPrice().compareTo(BigDecimal.valueOf(30000))).isZero();
    }
}
