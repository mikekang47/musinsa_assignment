package io.github.hoo47.musinsa_assignment.application.brand.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record BrandCreateRequest(
        @NotEmpty
        String name
) {
}
