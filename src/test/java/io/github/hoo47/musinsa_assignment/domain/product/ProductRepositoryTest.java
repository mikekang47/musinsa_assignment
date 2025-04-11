package io.github.hoo47.musinsa_assignment.domain.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import io.github.hoo47.musinsa_assignment.domain.product.dto.CategoryMinPrice;

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
    private Brand brandA;
    private Brand brandB;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
        
        // Set up categories
        category1 = categoryRepository.save(Category.builder().name("Top").build());
        category2 = categoryRepository.save(Category.builder().name("Bottom").build());
        
        // Set up brands
        brandA = brandRepository.save(Brand.builder().name("BrandA").build());
        brandB = brandRepository.save(Brand.builder().name("BrandB").build());
        
        // Set up products
        product1 = productRepository.save(Product.builder()
                .category(category1)
                .brand(brandA)
                .price(new BigDecimal("10000"))
                .build());
        
        product2 = productRepository.save(Product.builder()
                .category(category1)
                .brand(brandB)
                .price(new BigDecimal("5000"))  // Lowest price for Top category from BrandB
                .build());
        
        product3 = productRepository.save(Product.builder()
                .category(category2)
                .brand(brandA)
                .price(new BigDecimal("15000"))  // Lowest price for Bottom category from BrandA
                .build());
        
        product4 = productRepository.save(Product.builder()
                .category(category2)
                .brand(brandB)
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
    @DisplayName("카테고리별 최저가격을 조회할 수 있다")
    void findMinPricesByCategoriesTest() {
        // given
        List<Long> categoryIds = List.of(category1.getId(), category2.getId());
        
        // when
        List<CategoryMinPrice> result = productRepository.findMinPricesByCategories(categoryIds);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("categoryId", "minPrice")
                .contains(
                        tuple(category1.getId(), new BigDecimal("5000")),
                        tuple(category2.getId(), new BigDecimal("15000"))
                );
        
        // Category1(Top) has a minimum price of 5000
        CategoryMinPrice category1MinPrice = result.stream()
                .filter(minPrice -> minPrice.categoryId().equals(category1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(category1MinPrice.minPrice()).isEqualTo(new BigDecimal("5000"));
        
        // Category2(Bottom) has a minimum price of 15000
        CategoryMinPrice category2MinPrice = result.stream()
                .filter(minPrice -> minPrice.categoryId().equals(category2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(category2MinPrice.minPrice()).isEqualTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("카테고리 ID와 가격으로 상품을 조회할 수 있다")
    void findProductsByCategoryIdAndPriceTest() {
        // when
        List<Product> products = productRepository.findProductsByCategoryIdAndPrice(
                category1.getId(), new BigDecimal("5000"));

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(products.get(0).getCategory().getId()).isEqualTo(category1.getId());
        assertThat(products.get(0).getBrand().getId()).isEqualTo(brandB.getId());
    }

    @Test
    @DisplayName("브랜드별 카테고리 최저가 상품을 조회한다")
    void findCheapestProductsGroupByBrandAndCategory() {
        // given
        
        // when
        List<BrandCategoryPriceInfo> results = productRepository.findCheapestProductsGroupByBrandAndCategory();
        
        // then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(4);  // 2 brands x 2 categories = 4 results
        
        // Filter results by brand
        Map<Long, List<BrandCategoryPriceInfo>> resultsByBrand = results.stream()
                .collect(Collectors.groupingBy(BrandCategoryPriceInfo::brandId));
        
        assertThat(resultsByBrand).hasSize(2);
        assertThat(resultsByBrand).containsKeys(brandA.getId(), brandB.getId());
        
        // Verify BrandA results
        List<BrandCategoryPriceInfo> brandAResults = resultsByBrand.get(brandA.getId());
        assertThat(brandAResults).hasSize(2);
        assertThat(brandAResults).extracting("categoryId", "price")
                .contains(tuple(category1.getId(), new BigDecimal("10000")),
                        tuple(category2.getId(), new BigDecimal("15000")));
        
        // Verify BrandB results
        List<BrandCategoryPriceInfo> brandBResults = resultsByBrand.get(brandB.getId());
        assertThat(brandBResults).hasSize(2);
        assertThat(brandBResults).extracting("categoryId", "price")
                .contains(tuple(category1.getId(), new BigDecimal("5000")),
                        tuple(category2.getId(), new BigDecimal("20000")));
    }

    @Test
    @DisplayName("카테고리명으로 최소가격을 조회할 수 있다")
    void findMinPriceByCategoryNameTest() {
        // when
        BigDecimal minPrice = productRepository.findMinPriceByCategoryName("Top");

        // then
        assertThat(minPrice).isNotNull();
        assertThat(minPrice).isEqualByComparingTo(new BigDecimal("5000"));
    }

    @Test
    @DisplayName("카테고리명으로 최대가격을 조회할 수 있다")
    void findMaxPriceByCategoryNameTest() {
        // when
        BigDecimal maxPrice = productRepository.findMaxPriceByCategoryName("Top");

        // then
        assertThat(maxPrice).isNotNull();
        assertThat(maxPrice).isEqualByComparingTo(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("카테고리명과 가격으로 상품을 조회할 수 있다")
    void findProductsByCategoryNameAndPriceTest() {
        // when
        List<Product> products = productRepository.findProductsByCategoryNameAndPrice("Top", new BigDecimal("10000"));

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(products.get(0).getCategory().getName()).isEqualTo("Top");
        assertThat(products.get(0).getBrand().getName()).isEqualTo("BrandA");
    }

    /**
     * Helper method to find price info by category ID
     */
    private Optional<BrandCategoryPriceInfo> findPriceInfoByCategoryId(
            List<BrandCategoryPriceInfo> priceInfos, Long categoryId) {
        return priceInfos.stream()
                .filter(info -> info.categoryId().equals(categoryId))
                .findFirst();
    }
} 
