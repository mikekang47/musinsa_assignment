package io.github.hoo47.musinsa_assignment.controller.v1.brand;

import io.github.hoo47.musinsa_assignment.application.brand.BrandCommandService;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.dto.response.BrandResponse;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/brands")
@RestController
@RequiredArgsConstructor
public class BrandController {

    private final BrandCommandService brandCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse create(@RequestBody @Valid BrandCreateRequest request) {
        Brand brand = brandCommandService.createBrand(request);
        return new BrandResponse(brand.getId(), brand.getName());
    }
}
