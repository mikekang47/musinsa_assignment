package io.github.hoo47.musinsa_assignment.controller.v1.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandUpdateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.service.BrandCommandService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.usecase.BrandLowestPriceUsecase;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BrandCommandService brandCommandService;

    @MockBean
    private BrandLowestPriceUsecase brandLowestPriceUsecase;

    @Test
    @DisplayName("should create brand when request is valid")
    void shouldCreateBrandWhenRequestIsValid() throws Exception {
        // given
        BrandCreateRequest request = new BrandCreateRequest("NewBrand");
        Brand savedBrand = new Brand(1L, "NewBrand");
        given(brandCommandService.createBrand(any(BrandCreateRequest.class))).willReturn(savedBrand);

        // when/then
        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("NewBrand"));
    }

    @Test
    @DisplayName("should return bad request when brand name is missing")
    void shouldReturnBadRequestWhenBrandNameIsMissing() throws Exception {
        // given an invalid request with empty brand name
        BrandCreateRequest request = new BrandCreateRequest("");

        // when/then
        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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
        mockMvc.perform(get("/api/v1/brands/lowest-price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowestPrice.brandName").value("C브랜드"))
                .andExpect(jsonPath("$.lowestPrice.categories[0].categoryName").value("상의"))
                .andExpect(jsonPath("$.lowestPrice.categories[0].price").value(10000))
                .andExpect(jsonPath("$.lowestPrice.categories[1].categoryName").value("하의"))
                .andExpect(jsonPath("$.lowestPrice.categories[1].price").value(20000))
                .andExpect(jsonPath("$.lowestPrice.totalPrice").value(30000));
    }

    @Test
    @DisplayName("should return bad request error when service throws exception")
    void shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {
        // given
        BrandCreateRequest request = new BrandCreateRequest("");

        // when/then
        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update brand via patch when patch request is valid")
    void shouldUpdateBrandViaPatchWhenPatchIsValid() throws Exception {
        Long brandId = 1L;
        String validPatch = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"Updated Brand\"}]";
        Brand updatedBrand = new Brand(brandId, "Updated Brand");
        given(brandCommandService.updateBrand(brandId, new BrandUpdateRequest("Updated Brand")))
                .willReturn(updatedBrand);

        mockMvc.perform(patch("/api/v1/brands/{id}", brandId)
                        .contentType("application/json-patch+json")
                        .content(validPatch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(brandId))
                .andExpect(jsonPath("$.name").value("Updated Brand"));
    }

    @Test
    @DisplayName("should return bad request when patch request has invalid format")
    void shouldReturnBadRequestWhenPatchFormatIsInvalid() throws Exception {
        Long brandId = 1L;
        // Invalid patch because it is not an array
        String invalidPatch = "{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"Updated Brand\"}";

        mockMvc.perform(patch("/api/v1/brands/{id}", brandId)
                        .contentType("application/json-patch+json")
                        .content(invalidPatch))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return bad request when patched brand name is empty")
    void shouldReturnBadRequestWhenPatchedBrandNameIsEmpty() throws Exception {
        Long brandId = 1L;
        // Patch attempts to set the name to an empty string
        String patchWithEmptyName = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"\"}]";

        mockMvc.perform(patch("/api/v1/brands/{id}", brandId)
                        .contentType("application/json-patch+json")
                        .content(patchWithEmptyName))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return not found when brand does not exist")
    void shouldReturnNotFoundWhenBrandDoesNotExist() throws Exception {
        Long brandId = 999L;
        String validPatch = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"Updated Brand\"}]";

        // Simulate that the brand does not exist
        given(brandCommandService.updateBrand(brandId, new BrandUpdateRequest("Updated Brand")))
                .willThrow(new BusinessException(BusinessErrorCode.BRAND_NOT_FOUND));

        mockMvc.perform(patch("/api/v1/brands/{id}", brandId)
                        .contentType("application/json-patch+json")
                        .content(validPatch))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete brand when brand exists")
    void shouldDeleteBrandWhenBrandExists() throws Exception {
        Long brandId = 1L;

        // when/then
        mockMvc.perform(delete("/api/v1/brands/{id}", brandId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return not found when brand does not exist")
    void deleteBrandShouldReturnNotFoundWhenBrandDoesNotExist() throws Exception {
        Long brandId = 999L;

        // Simulate that the brand does not exist
        given(brandCommandService.deleteBrand(brandId))
                .willThrow(new BusinessException(BusinessErrorCode.BRAND_NOT_FOUND));

        mockMvc.perform(delete("/api/v1/brands/{id}", brandId))
                .andExpect(status().isNotFound());
    }
}
