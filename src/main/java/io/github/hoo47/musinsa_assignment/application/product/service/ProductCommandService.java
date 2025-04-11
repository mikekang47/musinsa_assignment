package io.github.hoo47.musinsa_assignment.application.product.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductQueryService productQueryService;

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
        
        Product savedProduct = productRepository.save(product);
        
        // Invalidate price cache
        productQueryService.clearPriceCache();
        
        return savedProduct;
    }

    public Product updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = findProductWithLock(productId);
        boolean priceChanged = false;
        boolean categoryChanged = false;
        boolean brandChanged = false;

        if (request.categoryId() != null) {
            Category category = categoryRepository.findByIdWithReadLock(request.categoryId())
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.CATEGORY_NOT_FOUND));
            if (!category.getId().equals(product.getCategory().getId())) {
                product.updateCategory(category);
                categoryChanged = true;
            }
        }

        if (request.brandId() != null) {
            Brand brand = brandRepository.findByIdWithReadLock(request.brandId())
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.BRAND_NOT_FOUND));
            if (!brand.getId().equals(product.getBrand().getId())) {
                product.updateBrand(brand);
                brandChanged = true;
            }
        }

        if (request.price() != null) {
            if (request.price().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(BusinessErrorCode.INVALID_PRICE);
            }
            if (request.price().compareTo(product.getPrice()) != 0) {
                product.updatePrice(request.price());
                priceChanged = true;
            }
        }
        
        // Only invalidate cache when price, category, or brand changes
        if (priceChanged || categoryChanged || brandChanged) {
            productQueryService.clearPriceCache();
        }

        return product;
    }

    public Product deleteProduct(Long productId) {
        Product product = findProductWithLock(productId);

        productRepository.deleteById(productId);
        
        // Invalidate price cache
        productQueryService.clearPriceCache();
        return product;
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
