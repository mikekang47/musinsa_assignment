package io.github.hoo47.musinsa_assignment.application.brand;

import io.github.hoo47.musinsa_assignment.application.brand.dto.request.BrandCreateRequest;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
@SpringBootTest
class BrandCommandServiceTest {

    @Autowired
    private BrandCommandService brandCommandService;

    @Test
    @DisplayName("브랜드 생성 테스트")
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
}
