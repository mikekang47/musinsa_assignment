package io.github.hoo47.musinsa_assignment.application.product.service;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductResponse;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    // TODO: make usecase layer
    private final ProductRepository productRepository;
    private final CategoryQueryService categoryQueryService;

    public List<CategoryProductResponse> findAllCategoryProducts() {
        return categoryQueryService.getAllCategories().stream()
                .flatMap(category -> productRepository.findByCategoryWithFetch(category.getId())
                        .stream()
                        .map(CategoryProductResponse::from))
                .toList();
    }

    // TODO: Implement query methods
} 
