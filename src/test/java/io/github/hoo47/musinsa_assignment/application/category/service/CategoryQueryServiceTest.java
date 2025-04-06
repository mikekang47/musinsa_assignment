package io.github.hoo47.musinsa_assignment.application.category.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;

@SpringBootTest
@ActiveProfiles("test")
class CategoryQueryServiceTest {

    @Autowired
    private CategoryQueryService categoryQueryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        categoryRepository.deleteAllInBatch();

        categoryRepository.save(Category.builder()
                .name("상의")
                .build());

        categoryRepository.save(Category.builder()
                .name("아우터")
                .build());

        categoryRepository.save(Category.builder()
                .name("바지")
                .build());
    }

    @Test
    @Transactional
    @DisplayName("모든 카테고리를 조회할 수 있다")
    void getAllCategories() {
        // when
        List<Category> categories = categoryQueryService.getAllCategories();

        // then
        assertThat(categories).hasSize(3);
        assertThat(categories)
                .extracting("name")
                .containsExactlyInAnyOrder("상의", "아우터", "바지");
    }
}
