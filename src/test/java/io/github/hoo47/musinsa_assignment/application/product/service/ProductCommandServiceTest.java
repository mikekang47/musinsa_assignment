package io.github.hoo47.musinsa_assignment.application.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductCreateRequest;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.product.Product;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductCommandServiceTest {

    @Autowired
    private ProductCommandService productCommandService;

    @Test
    @DisplayName("상품을 생성할 수 있다")
    void createProduct() {
        // given
        ProductCreateRequest request = new ProductCreateRequest("A", 1L, BigDecimal.valueOf(10000));

        // when
        Product product = productCommandService.createProduct(request);

        // then
        assertThat(product).isNotNull();
        assertThat(product.getBrand()).isEqualTo("A");
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(product.getCategory().getName()).isEqualTo("상의");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 생성할 수 없다")
    void createProduct_CategoryNotFound() {
        // given
        ProductCreateRequest request = new ProductCreateRequest("A", 999L, BigDecimal.valueOf(10000));

        // when & then
        assertThatThrownBy(() -> productCommandService.createProduct(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("상품 가격은 0보다 커야 한다")
    void createProduct_InvalidPrice() {
        // given
        ProductCreateRequest request = new ProductCreateRequest("A", 1L, BigDecimal.ZERO);

        // when & then
        assertThatThrownBy(() -> productCommandService.createProduct(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_PRICE);
    }
}
