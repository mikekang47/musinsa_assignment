package io.github.hoo47.musinsa_assignment.application.brand.service;

import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandUpdateRequest;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandCommandService {

    private final BrandRepository brandRepository;

    public Brand createBrand(BrandCreateRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_BRAND_NAME);
        }

        Brand brand = new Brand(request.name());

        return brandRepository.save(brand);
    }

    public Brand updateBrand(Long brandId, BrandUpdateRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_BRAND_NAME);
        }

        Brand brand = findBrand(brandId);

        brand.updateName(request.name());

        return brand;
    }

    public Brand deleteBrand(Long brandId) {
        Brand brand = findBrand(brandId);

        brandRepository.deleteById(brandId);

        return brand;
    }

    // TODO: Implement locking mechanism
    private Brand findBrand(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BRAND_NOT_FOUND));
    }

}
