package io.github.hoo47.musinsa_assignment.application.usecase;

import io.github.hoo47.musinsa_assignment.application.brand.dto.response.CategoryPriceSummaryResponse;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CategoryPriceSummaryUsecaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryPriceSummaryUsecase usecase;

    private Category category;
    private Brand brand1;
    private Brand brand2;

    @BeforeEach
    void setUp() {
        // Clear data if necessary
        productRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create and save a category.
        category = Category.builder()
                .name("상의")
                .build();
        categoryRepository.save(category);

        // Create and save two brands.
        brand1 = Brand.builder()
                .name("Brand1")
                .build();
        brand2 = Brand.builder()
                .name("BrandB")
                .build();
        brandRepository.saveAll(List.of(brand1, brand2));

        // Insert products:
        // Brand1 has two products with the lowest price
        Product productA1 = Product.builder()
                .category(category)
                .brand(brand1)
                .price(BigDecimal.valueOf(10000))
                .build();
        Product productA2 = Product.builder()
                .category(category)
                .brand(brand1)
                .price(BigDecimal.valueOf(10000))
                .build();
        // BrandB has one product with the highest price
        Product productB = Product.builder()
                .category(category)
                .brand(brand2)
                .price(BigDecimal.valueOf(30000))
                .build();

        productRepository.saveAll(List.of(productA1, productA2, productB));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testGetPriceSummaryByCategoryName() {
        CategoryPriceSummaryResponse response = usecase.getPriceSummaryByCategoryName("상의");

        // Verify the response is not null and contains correct category name
        assertThat(response).isNotNull();
        assertThat(response.category()).isEqualTo("상의");

        // Verify lowest price products from Brand1 are returned
        assertThat(response.lowestPrice()).hasSize(2);
        response.lowestPrice().forEach(info -> {
            assertThat(info.brand()).isEqualTo("Brand1");
            assertThat(info.price()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        });

        // Verify highest price product from BrandB is returned
        assertThat(response.highestPrice()).hasSize(1);
        CategoryPriceSummaryResponse.PriceInfo highest = response.highestPrice().get(0);
        assertThat(highest.brand()).isEqualTo("BrandB");
        assertThat(highest.price()).isEqualByComparingTo(BigDecimal.valueOf(30000));
    }
}
