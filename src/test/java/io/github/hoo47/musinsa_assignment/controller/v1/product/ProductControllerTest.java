package io.github.hoo47.musinsa_assignment.controller.v1.product;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductCreateRequest;
import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductUpdateRequest;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductCommandService;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.product.Product;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductCommandService productCommandService;

    @Test
    @DisplayName("should create product when valid product create request is provided")
    void shouldCreateProductWhenRequestIsValid() throws Exception {
        // given
        ProductCreateRequest request = new ProductCreateRequest(
                1L, // assume brand id
                1L, // assume category id
                new BigDecimal("10000")
        );

        Brand brand = new Brand(1L, "BrandA");
        Category category = new Category(1L, "CategoryA");
        Product createdProduct = Product.builder()
                .id(1L)
                .price(new BigDecimal("10000"))
                .brand(brand)
                .category(category)
                .build();

        given(productCommandService.createProduct(any(ProductCreateRequest.class)))
                .willReturn(createdProduct);

        // when/then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.brand.id").value(1L))
                .andExpect(jsonPath("$.brand.name").value("BrandA"))
                .andExpect(jsonPath("$.category.id").value(1L))
                .andExpect(jsonPath("$.category.name").value("CategoryA"));
    }

    @Test
    @DisplayName("should return bad request when product create request is invalid")
    void shouldReturnBadRequestWhenProductCreateRequestIsInvalid() throws Exception {
        // given a request missing the price field (assuming price is required)
        ProductCreateRequest invalidRequest = new ProductCreateRequest(
                1L, // category id
                1L, // brand id
                null // missing price
        );

        // when/then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("should update product when valid JSON patch is provided")
    void shouldUpdateProductWhenValidJsonPatchProvided() throws Exception {
        Long productId = 1L;
        // valid patch to update price to 20000
        String validPatch = "[{\"op\":\"replace\",\"path\":\"/price\",\"value\":20000}]";
        Brand brand = new Brand(1L, "BrandA");
        Category category = new Category(1L, "CategoryA");
        Product updatedProduct = Product.builder()
                .id(productId)
                .price(new BigDecimal("20000"))
                .brand(brand)
                .category(category)
                .build();

        ProductUpdateRequest updateRequest = new ProductUpdateRequest(null, null, BigDecimal.valueOf(20000));
        given(productCommandService.updateProduct(eq(productId), any(ProductUpdateRequest.class)))
                .willReturn(updatedProduct);

        mockMvc.perform(patch("/api/v1/products/{id}", productId)
                        .contentType("application/json-patch+json")
                        .content(validPatch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.price").value(20000))
                .andExpect(jsonPath("$.brand.id").value(1L))
                .andExpect(jsonPath("$.brand.name").value("BrandA"))
                .andExpect(jsonPath("$.category.id").value(1L))
                .andExpect(jsonPath("$.category.name").value("CategoryA"));
    }

    @Test
    @DisplayName("should return bad request when JSON patch format is invalid")
    void shouldReturnBadRequestWhenJsonPatchFormatIsInvalid() throws Exception {
        Long productId = 1L;
        // invalid patch because it is not an array
        String invalidPatch = "{\"op\":\"replace\",\"path\":\"/price\",\"value\":20000}";

        mockMvc.perform(patch("/api/v1/products/{id}", productId)
                        .contentType("application/json-patch+json")
                        .content(invalidPatch))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return bad request when patched price is negative")
    void shouldReturnBadRequestWhenPatchedPriceIsNegative() throws Exception {
        Long productId = 1L;
        // valid patch but sets price to negative value
        String negativePricePatch = "[{\"op\":\"replace\",\"path\":\"/price\",\"value\":-100}]";

        mockMvc.perform(patch("/api/v1/products/{id}", productId)
                        .contentType("application/json-patch+json")
                        .content(negativePricePatch))
                .andExpect(status().isBadRequest());
    }
}
