package io.github.hoo47.musinsa_assignment.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("모든 상품을 조회할 수 있다")
    void findAllTest() {
        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).isNotEmpty();
        assertThat(products.size()).isGreaterThan(0);

        // 브랜드별로 카테고리마다 1개씩, 총 9개 브랜드 * 8개 카테고리 = 72개 상품이 있어야 함
        assertThat(products.size()).isEqualTo(72);
    }
} 
