package io.github.hoo47.musinsa_assignment.application.product.service;

import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductCreateRequest;
import io.github.hoo47.musinsa_assignment.application.product.dto.request.ProductUpdateRequest;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessErrorCode;
import io.github.hoo47.musinsa_assignment.common.exception.BusinessException;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.brand.BrandRepository;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import io.github.hoo47.musinsa_assignment.domain.category.CategoryRepository;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public Product createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CATEGORY_NOT_FOUND));

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BRAND_NOT_FOUND));

        if (request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(BusinessErrorCode.INVALID_PRICE);
        }

        Product product = Product.builder()
                .price(request.price())
                .category(category)
                .brand(brand)
                .build();
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = findProductWithLock(productId);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findByIdWithReadLock(request.categoryId())
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.CATEGORY_NOT_FOUND));
            product.updateCategory(category);
        }

        if (request.brandId() != null) {
            Brand brand = brandRepository.findByIdWithReadLock(request.brandId())
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.BRAND_NOT_FOUND));
            product.updateBrand(brand);
        }

        if (request.price() != null) {
            if (request.price().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(BusinessErrorCode.INVALID_PRICE);
            }
            product.updatePrice(request.price());
        }

        return product;
    }

    @Transactional
    public Product deleteProduct(Long productId) {
        Product product = findProductWithLock(productId);

        productRepository.deleteById(productId);
        return product;
    }

    /**
     * Retrieves a product by its ID.
     * Used for read-only operations.
     */
    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));
    }

    /**
     * Retrieves a product by its ID with a pessimistic read lock.
     * Used for update and delete operations to prevent concurrent modifications.
     */
    private Product findProductWithLock(Long productId) {
        return productRepository.findByIdWithReadLock(productId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));
    }
}
