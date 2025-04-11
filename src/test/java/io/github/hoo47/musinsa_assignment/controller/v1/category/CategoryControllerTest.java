package io.github.hoo47.musinsa_assignment.controller.v1.category;

import io.github.hoo47.musinsa_assignment.application.brand.dto.response.CategoryPriceSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.usecase.BrandLowestPriceUsecase;
import io.github.hoo47.musinsa_assignment.application.usecase.CategoryPriceSummaryUsecase;
import io.github.hoo47.musinsa_assignment.application.usecase.CategoryProductPriceUsecase;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryProductPriceUsecase categoryProductPriceUsecase;

    @MockBean
    private BrandLowestPriceUsecase brandLowestPriceUsecase;

    @MockBean
    private CategoryPriceSummaryUsecase categoryPriceSummaryUsecase;

    @Test
    @DisplayName("카테고리별 최저가격 브랜드와 상품 가격, 총액을 조회할 수 있다")
    void getCategoryPricing() throws Exception {
        // given
        var categoryInfo1 = new CategoryProductSummaryResponse.CategoryProductPriceInfo(
                1L, "상의", 1L, "A브랜드", new BigDecimal("10000"));
        var categoryInfo2 = new CategoryProductSummaryResponse.CategoryProductPriceInfo(
                2L, "하의", 2L, "B브랜드", new BigDecimal("20000"));

        var response = new CategoryProductSummaryResponse(
                List.of(categoryInfo1, categoryInfo2),
                new BigDecimal("30000")
        );

        given(categoryProductPriceUsecase.getCategoryPricing()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/categories/lowest-price-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].categoryId").value(1))
                .andExpect(jsonPath("$.items[0].categoryName").value("상의"))
                .andExpect(jsonPath("$.items[0].brandId").value(1))
                .andExpect(jsonPath("$.items[0].brandName").value("A브랜드"))
                .andExpect(jsonPath("$.items[0].price").value(10000))
                .andExpect(jsonPath("$.items[1].categoryId").value(2))
                .andExpect(jsonPath("$.items[1].categoryName").value("하의"))
                .andExpect(jsonPath("$.items[1].brandId").value(2))
                .andExpect(jsonPath("$.items[1].brandName").value("B브랜드"))
                .andExpect(jsonPath("$.items[1].price").value(20000))
                .andExpect(jsonPath("$.totalPrice").value(30000));
    }

    @Test
    @DisplayName("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드와 가격을 조회할 수 있다")
    void getLowestBrandPrice() throws Exception {
        // given
        var categories = Arrays.asList(
                new BrandProductSummaryResponse.CategoryPrice("상의", new BigDecimal("10000")),
                new BrandProductSummaryResponse.CategoryPrice("하의", new BigDecimal("20000"))
        );

        var lowestPriceInfo = new BrandProductSummaryResponse.LowestPriceInfo(
                "C브랜드", categories, new BigDecimal("30000")
        );

        var response = new BrandProductSummaryResponse(lowestPriceInfo);

        given(brandLowestPriceUsecase.getBrandWithLowestTotalPrice()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/categories/lowest-brand-price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowestPrice.brandName").value("C브랜드"))
                .andExpect(jsonPath("$.lowestPrice.categories[0].categoryName").value("상의"))
                .andExpect(jsonPath("$.lowestPrice.categories[0].price").value(10000))
                .andExpect(jsonPath("$.lowestPrice.categories[1].categoryName").value("하의"))
                .andExpect(jsonPath("$.lowestPrice.categories[1].price").value(20000))
                .andExpect(jsonPath("$.lowestPrice.totalPrice").value(30000));
    }

    @Test
    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회할 수 있다")
    void getCategoryPriceSummary() throws Exception {
        // given
        String categoryName = "상의";

        var lowestPrices = List.of(
                new CategoryPriceSummaryResponse.PriceInfo("A브랜드", new BigDecimal("10000")),
                new CategoryPriceSummaryResponse.PriceInfo("B브랜드", new BigDecimal("10000"))
        );

        var highestPrices = List.of(
                new CategoryPriceSummaryResponse.PriceInfo("C브랜드", new BigDecimal("50000"))
        );

        var response = new CategoryPriceSummaryResponse(categoryName, lowestPrices, highestPrices);

        given(categoryPriceSummaryUsecase.getPriceSummaryByCategoryName(categoryName)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/categories/{categoryName}/price-summary", categoryName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("상의"))
                .andExpect(jsonPath("$.lowestPrice[0].brand").value("A브랜드"))
                .andExpect(jsonPath("$.lowestPrice[0].price").value(10000))
                .andExpect(jsonPath("$.lowestPrice[1].brand").value("B브랜드"))
                .andExpect(jsonPath("$.lowestPrice[1].price").value(10000))
                .andExpect(jsonPath("$.highestPrice[0].brand").value("C브랜드"))
                .andExpect(jsonPath("$.highestPrice[0].price").value(50000));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 가격 요약 조회시 오류가 발생한다")
    void getCategoryPriceSummaryWithNonExistingCategory() throws Exception {
        // given
        String nonExistingCategory = "존재하지않는카테고리";

        given(categoryPriceSummaryUsecase.getPriceSummaryByCategoryName(nonExistingCategory))
                .willThrow(new BusinessException(BusinessErrorCode.CATEGORY_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/categories/{categoryName}/price-summary", nonExistingCategory))
                .andExpect(status().isNotFound());
    }
} 
