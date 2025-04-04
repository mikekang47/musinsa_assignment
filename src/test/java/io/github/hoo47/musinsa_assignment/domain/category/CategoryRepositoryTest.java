package io.github.hoo47.musinsa_assignment.domain.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("ID로 카테고리를 조회할 수 있다")
    void findById() {
        // given
        // data.sql에 의해서 저장되어 있음.
        Long categoryId = 1L;

        // when
        var category = categoryRepository.findById(categoryId);

        // then
        assertThat(category).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 카테고리를 조회하면 빈 Optional을 반환한다")
    void findById_NotFound() {
        // given
        Long notExistsCategoryId = 999L;

        // when
        var category = categoryRepository.findById(notExistsCategoryId);

        // then
        assertThat(category).isEmpty();
    }
} 
