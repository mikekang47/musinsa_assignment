package io.github.hoo47.musinsa_assignment.controller.v1.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductCreateRequest;
import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductUpdateRequest;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductCommandService;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.controller.v1.product.dto.response.ProductResponse;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequestMapping("/api/v1/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody @Valid ProductCreateRequest request) {
        Product product = productCommandService.createProduct(request);
        return ProductResponse.of(
                product.getId(),
                product.getPrice(),
                new ProductResponse.Brand(
                        product.getBrand().getId(),
                        product.getBrand().getName()
                ),
                new ProductResponse.Category(
                        product.getCategory().getId(),
                        product.getCategory().getName()
                )
        );
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody JsonNode patchNode
    ) {

        JsonPatch patch;
        try {
            patch = JsonPatch.fromJson(patchNode);
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorCode.INVALID_PATCH_REQUEST);
        }

        ProductUpdateRequest emptyTarget = new ProductUpdateRequest(null, null, null);
        ProductUpdateRequest patchedDto;
        try {
            JsonNode patchedNode = patch.apply(objectMapper.convertValue(emptyTarget, JsonNode.class));
            patchedDto = objectMapper.treeToValue(patchedNode, ProductUpdateRequest.class);
            if (patchedDto.price() != null && patchedDto.price().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(BusinessErrorCode.INVALID_PRICE);
            }
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new BusinessException(BusinessErrorCode.INVALID_PATCH_REQUEST);
        }

        Product product = productCommandService.updateProduct(id, patchedDto);

        return ProductResponse.of(
                product.getId(),
                product.getPrice(),
                new ProductResponse.Brand(
                        product.getBrand().getId(),
                        product.getBrand().getName()
                ),
                new ProductResponse.Category(
                        product.getCategory().getId(),
                        product.getCategory().getName()
                )
        );
    }

}
