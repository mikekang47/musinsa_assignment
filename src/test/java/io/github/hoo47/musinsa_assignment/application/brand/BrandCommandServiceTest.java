package io.github.hoo47.musinsa_assignment.application.brand;

import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandUpdateRequest;
import io.github.hoo47.musinsa_assignment.application.brand.service.BrandCommandService;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@ActiveProfiles("test")
class BrandCommandServiceTest {

    @Autowired
    private BrandCommandService brandCommandService;
    @Autowired
    private BrandRepository brandRepository;

    @AfterEach
    void tearDown() {
        brandRepository.deleteAll();
    }

    @Test
    @DisplayName("브랜드 생성할 수 있다.")
    void createBrand() {
        // given
        String brandName = "Test Brand";

        // when
        var brand = brandCommandService.createBrand(new BrandCreateRequest(brandName));

        // then
        assertThat(brand.getId()).isNotNull();
        assertThat(brand.getName()).isEqualTo(brandName);
    }

    @Test
    @DisplayName("브랜드 생성 시 이름이 비어있으면 예외가 발생한다.")
    void createBrandWithEmptyName() {
        // given
        String brandName = "";

        // when & then
        assertThatThrownBy(() -> brandCommandService.createBrand(new BrandCreateRequest(brandName)))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_BRAND_NAME);


    }

    @Test
    @DisplayName("브랜드를 수정할 수 있다.")
    void updateBrand() throws IOException {
        // given
        String brandName = "Test Brand";
        var brand = brandCommandService.createBrand(new BrandCreateRequest(brandName));

        // when
        String updatedBrandName = "Updated Brand";
        var updatedBrand = brandCommandService.updateBrand(brand.getId(), new BrandUpdateRequest(updatedBrandName));

        // then
        assertThat(updatedBrand.getId()).isEqualTo(brand.getId());
        assertThat(updatedBrand.getName()).isEqualTo(updatedBrandName);
    }

    @Test
    @DisplayName("브랜드 수정 시 이름이 비어있으면 예외가 발생한다.")
    void updateBrandWithEmptyName() {
        // given
        String brandName = "Test Brand";
        var brand = brandCommandService.createBrand(new BrandCreateRequest(brandName));
        var invalidBrandName = "";
        var request = new BrandUpdateRequest(invalidBrandName);

        // when & then
        assertThatThrownBy(() -> brandCommandService.updateBrand(brand.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_BRAND_NAME);
    }

    @Test
    @DisplayName("브랜드 수정 시 존재하지 않는 브랜드 ID로 요청하면 예외가 발생한다.")
    void updateBrandWithNonExistentId() {
        // given
        String brandName = "Test Brand";
        var brand = brandCommandService.createBrand(new BrandCreateRequest(brandName));
        var notExistentBrandId = brand.getId() + 1;
        var request = new BrandUpdateRequest("Updated Brand");

        // when & then
        assertThatThrownBy(() -> brandCommandService.updateBrand(notExistentBrandId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.BRAND_NOT_FOUND);
    }

    @Test
    @DisplayName("브랜드를 삭제할 수 있다.")
    void deleteBrand() {
        // given
        String brandName = "Test Brand";
        var brand = brandCommandService.createBrand(new BrandCreateRequest(brandName));

        // when
        var deletedBrand = brandCommandService.deleteBrand(brand.getId());

        // then
        assertThat(deletedBrand.getId()).isEqualTo(brand.getId());
        assertThat(deletedBrand.getName()).isEqualTo(brand.getName());

        brandRepository.findById(brand.getId()).isEmpty();
    }

    @Test
    @DisplayName("브랜드 삭제 시 존재하지 않는 브랜드 ID로 요청하면 예외가 발생한다.")
    void deleteBrandWithNonExistentId() {
        // given
        String brandName = "Test Brand";
        var brand = brandCommandService.createBrand(new BrandCreateRequest(brandName));
        var notExistentBrandId = brand.getId() + 1;

        // when & then
        assertThatThrownBy(() -> brandCommandService.deleteBrand(notExistentBrandId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.BRAND_NOT_FOUND);
    }
}
