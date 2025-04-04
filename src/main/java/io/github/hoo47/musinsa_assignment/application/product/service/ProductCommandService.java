package io.github.hoo47.musinsa_assignment.application.product.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductCreateRequest;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CATEGORY_NOT_FOUND));

        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BusinessErrorCode.INVALID_PRICE);
        }

        Product product = new Product(request.price(), category, request.brand());
        return productRepository.save(product);
    }
}
