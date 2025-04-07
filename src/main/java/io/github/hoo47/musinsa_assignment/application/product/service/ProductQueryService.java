package io.github.hoo47.musinsa_assignment.application.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    // TODO: make usecase layer
    private final ProductRepository productRepository;
    private final CategoryQueryService categoryQueryService;

    public List<Product> getCheapestProductInCategory(Long categoryId) {
        return productRepository.findTop1ByCategoryIdOrderByPriceAsc(categoryId);
    }
    // TODO: Implement query methods
} 
