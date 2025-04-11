package io.github.hoo47.musinsa_assignment.controller.v1.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hoo47.musinsa_assignment.application.brand.BrandCommandService;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BrandCommandService brandCommandService;

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
                .andExpect(status().isOk())
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
}
