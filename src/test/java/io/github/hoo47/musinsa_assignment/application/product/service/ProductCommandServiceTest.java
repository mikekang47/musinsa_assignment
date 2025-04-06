package io.github.hoo47.musinsa_assignment.application.product.service;

import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductCreateRequest;
import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductUpdateRequest;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ProductCommandServiceTest {

    @Autowired
    private ProductCommandService productCommandService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    private Product testProduct;
    private Category category;
    private Category anotherCategory;
    private Brand brand;
    private Brand anotherBrand;

    @BeforeEach
    @Transactional
    void setUp() {
        deleteAllData();
        
        // 카테고리 생성
        category = categoryRepository.save(Category.builder()
                .name("상의")
                .build());

        anotherCategory = categoryRepository.save(Category.builder()
                .name("아우터")
                .build());

        // 브랜드 생성
        brand = brandRepository.save(Brand.builder()
                .name("A")
                .build());

        anotherBrand = brandRepository.save(Brand.builder()
                .name("B")
                .build());

        // 테스트용 상품 생성
        testProduct = productRepository.save(Product.builder()
                .category(category)
                .brand(brand)
                .price(BigDecimal.valueOf(10000))
                .build());
    }

    @AfterEach
    @Transactional
    void tearDown() {
        deleteAllData();
    }

    private void deleteAllData() {
        productRepository.deleteAllInBatch();
        brandRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    @Transactional
    @DisplayName("상품을 생성할 수 있다")
    void createProduct() {
        // given
        ProductCreateRequest request = new ProductCreateRequest(
                brand.getId(),
                category.getId(),
                BigDecimal.valueOf(10000)
        );

        // when
        Product product = productCommandService.createProduct(request);

        // then
        assertThat(product).isNotNull();
        assertThat(product.getBrand().getName()).isEqualTo("A");
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(product.getCategory().getName()).isEqualTo("상의");
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 카테고리로 상품을 생성할 수 없다")
    void createProduct_CategoryNotFound() {
        // given
        ProductCreateRequest request = new ProductCreateRequest(
                brand.getId(),
                999L,
                BigDecimal.valueOf(10000)
        );

        // when & then
        assertThatThrownBy(() -> productCommandService.createProduct(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 브랜드로 상품을 생성할 수 없다")
    void createProduct_BrandNotFound() {
        // given
        ProductCreateRequest request = new ProductCreateRequest(
                999L,
                category.getId(),
                BigDecimal.valueOf(10000)
        );

        // when & then
        assertThatThrownBy(() -> productCommandService.createProduct(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.BRAND_NOT_FOUND);
    }

    @Test
    @Transactional
    @DisplayName("상품 가격은 0이상이어야 한다")
    void createProduct_InvalidPrice() {
        // given
        ProductCreateRequest request = new ProductCreateRequest(
                brand.getId(),
                category.getId(),
                BigDecimal.valueOf(-1)
        );

        // when & then
        assertThatThrownBy(() -> productCommandService.createProduct(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_PRICE);
    }

    @Test
    @Transactional
    @DisplayName("상품 가격을 0원으로 생성할 수 있다")
    void createProduct_ZeroPrice() {
        // given
        ProductCreateRequest request = new ProductCreateRequest(
                brand.getId(),
                category.getId(),
                BigDecimal.ZERO
        );

        // when
        Product product = productCommandService.createProduct(request);

        // then
        assertThat(product.getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @Transactional
    @DisplayName("상품 가격을 수정할 수 있다")
    void updatePrice() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(null, null, BigDecimal.valueOf(20000));

        // when
        Product updatedProduct = productCommandService.updateProduct(testProduct.getId(), request);

        // then
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(20000));
    }

    @Test
    @Transactional
    @DisplayName("상품 카테고리를 수정할 수 있다")
    void updateCategory() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(anotherCategory.getId(), null, null);

        // when
        Product updatedProduct = productCommandService.updateProduct(testProduct.getId(), request);

        // then
        assertThat(updatedProduct.getCategory().getId()).isEqualTo(anotherCategory.getId());
        assertThat(updatedProduct.getCategory().getName()).isEqualTo("아우터");
    }

    @Test
    @Transactional
    @DisplayName("상품 브랜드를 수정할 수 있다")
    void updateBrand() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(null, anotherBrand.getId(), null);

        // when
        Product updatedProduct = productCommandService.updateProduct(testProduct.getId(), request);

        // then
        assertThat(updatedProduct.getBrand().getId()).isEqualTo(anotherBrand.getId());
        assertThat(updatedProduct.getBrand().getName()).isEqualTo("B");
    }

    @Test
    @Transactional
    @DisplayName("여러 필드를 동시에 수정할 수 있다")
    void updateMultipleFields() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(
                anotherCategory.getId(),
                anotherBrand.getId(),
                BigDecimal.valueOf(20000)
        );

        // when
        Product updatedProduct = productCommandService.updateProduct(testProduct.getId(), request);

        // then
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(20000));
        assertThat(updatedProduct.getCategory().getId()).isEqualTo(anotherCategory.getId());
        assertThat(updatedProduct.getBrand().getId()).isEqualTo(anotherBrand.getId());
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 상품은 수정할 수 없다")
    void updateNonExistentProduct() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(null, null, BigDecimal.valueOf(20000));

        // when & then
        assertThatThrownBy(() -> productCommandService.updateProduct(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @Transactional
    @DisplayName("상품 가격을 0원으로 수정할 수 있다")
    void updatePrice_Zero() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(null, null, BigDecimal.ZERO);

        // when
        Product updatedProduct = productCommandService.updateProduct(testProduct.getId(), request);

        // then
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @Transactional
    @DisplayName("상품 가격을 음수로 수정할 수 없다")
    void updateWithInvalidPrice() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(null, null, BigDecimal.valueOf(-1));

        // when & then
        assertThatThrownBy(() -> productCommandService.updateProduct(testProduct.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_PRICE);
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 카테고리로 수정할 수 없다")
    void updateWithNonExistentCategory() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(999L, null, null);

        // when & then
        assertThatThrownBy(() -> productCommandService.updateProduct(testProduct.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 브랜드로 수정할 수 없다")
    void updateWithNonExistentBrand() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest(null, 999L, null);

        // when & then
        assertThatThrownBy(() -> productCommandService.updateProduct(testProduct.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.BRAND_NOT_FOUND);
    }

    @Test
    @Transactional
    @DisplayName("상품을 삭제할 수 있다")
    void deleteProduct() {
        // given
        Long productId = testProduct.getId();

        // when
        Product deletedProduct = productCommandService.deleteProduct(productId);

        // then
        assertThat(deletedProduct).isNotNull();
        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 상품을 삭제할 수 없다")
    void deleteNonExistentProduct() {
        // given
        Long nonExistentProductId = 999L;

        // when & then
        assertThatThrownBy(() -> productCommandService.deleteProduct(nonExistentProductId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.PRODUCT_NOT_FOUND);
    }
}
