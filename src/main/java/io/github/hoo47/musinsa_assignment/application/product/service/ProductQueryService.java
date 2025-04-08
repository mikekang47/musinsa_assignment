package io.github.hoo47.musinsa_assignment.application.product.service;

import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;

    public List<Product> getCheapestProductInCategory(List<Long> categoryIds) {
        return productRepository.findCheapestProductsByCategory(categoryIds);
    }
}
