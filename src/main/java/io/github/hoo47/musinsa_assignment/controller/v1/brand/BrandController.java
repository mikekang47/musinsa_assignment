package io.github.hoo47.musinsa_assignment.controller.v1.brand;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandUpdateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.dto.response.BrandResponse;
import io.github.hoo47.musinsa_assignment.application.brand.service.BrandCommandService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.application.usecase.BrandLowestPriceUsecase;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/brands")
@RestController
public class BrandController {

    private final BrandCommandService brandCommandService;
    private final BrandLowestPriceUsecase brandLowestPriceUsecase;
    private final ObjectMapper objectMapper;

    public BrandController(BrandCommandService brandCommandService, BrandLowestPriceUsecase brandLowestPriceUsecase) {
        this.brandCommandService = brandCommandService;
        this.brandLowestPriceUsecase = brandLowestPriceUsecase;
        this.objectMapper = new ObjectMapper() ;
    }



    @GetMapping("/lowest-price")
    public BrandProductSummaryResponse getLowestPriceBrand() {
        return brandLowestPriceUsecase.getBrandWithLowestTotalPrice();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse create(@RequestBody @Valid BrandCreateRequest request) {
        Brand brand = brandCommandService.createBrand(request);
        return new BrandResponse(brand.getId(), brand.getName());
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public BrandResponse updateUser(
            @PathVariable Long id,
            @RequestBody JsonNode patchNode) {

        JsonPatch patch;
        try {
            patch = JsonPatch.fromJson(patchNode);
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorCode.INVALID_PATCH_REQUEST);
        }

        BrandUpdateRequest emptyTarget = new BrandUpdateRequest(null);
        BrandUpdateRequest patchedDto;
        try {
            JsonNode patchedNode = patch.apply(objectMapper.convertValue(emptyTarget, JsonNode.class));
            patchedDto = objectMapper.treeToValue(patchedNode, BrandUpdateRequest.class);
            if (patchedDto.name() == null || patchedDto.name().isBlank()) {
                throw new BusinessException(BusinessErrorCode.INVALID_BRAND_NAME);
            }
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new BusinessException(BusinessErrorCode.INVALID_PATCH_REQUEST);
        }

        Brand brand = brandCommandService.updateBrand(id, patchedDto);

        return new BrandResponse(brand.getId(), brand.getName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        brandCommandService.deleteBrand(id);
    }
}
