package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

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

    @BeforeEach
    void setUp() {
        // 카테고리 생성
        Category category1 = categoryRepository.save(Category.builder()
                .name("상의")
                .build());


        // 브랜드 생성
        Brand brand1 = brandRepository.save(Brand.builder()
                .name("A")
                .build());

        productRepository.save(Product.builder()
                .price(BigDecimal.valueOf(10000))
                .category(category1)
                .brand(brand1)
                .build());
    }

    @Test
    @DisplayName("모든 상품을 조회할 수 있다")
    void findAllTest() {
        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).isNotEmpty();
        assertThat(products.size()).isGreaterThan(0);

        assertThat(products.size()).isEqualTo(1);
    }
} 
