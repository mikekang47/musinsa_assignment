package io.github.hoo47.musinsa_assignment.domain.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.save(Category.builder()
                .name("상의")
                .build());

        categoryRepository.save(Category.builder()
                .name("아우터")
                .build());
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("ID로 카테고리를 조회할 수 있다")
    void findById() {
        // given
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

    @Test
    @DisplayName("count")
    void count() {
        // given
        long expectedCount = 2L;

        // when
        long count = categoryRepository.count();

        // then
        assertThat(count).isEqualTo(expectedCount);
    }
} 
