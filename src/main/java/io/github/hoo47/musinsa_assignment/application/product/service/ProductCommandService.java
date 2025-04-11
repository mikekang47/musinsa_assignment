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
import io.github.hoo47.musinsa_assignment.common.exception.ResourceNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductQueryService productQueryService;

    @Override
    public Product createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));
        
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
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

    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        
        productRepository.deleteById(productId);
        
        // Invalidate price cache
        productQueryService.clearPriceCache();
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
