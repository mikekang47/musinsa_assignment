package io.github.hoo47.musinsa_assignment.application.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.domain.product.Product;
import io.github.hoo47.musinsa_assignment.domain.product.ProductRepository;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import io.github.hoo47.musinsa_assignment.domain.product.dto.CategoryMinPrice;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;
    
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final String PRICE_INFO_CACHE = "priceInfoCache";

    /**
     * Find the cheapest product in each category.
     * This method is optimized to reduce database load by executing two separate queries:
     * 1. First, get the minimum price for each category
     * 2. Then, fetch each product with the minimum price
     *
     * @param categoryIds List of category IDs to search
     * @return List of products with the lowest price in each category
     */
    @Cacheable(value = PRICE_INFO_CACHE, key = "'cheapestInCategories:' + #categoryIds")
    public List<Product> getCheapestProductInCategory(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        
        try {
            // Get min prices for each category
            List<CategoryMinPrice> minPrices = productRepository.findMinPricesByCategories(categoryIds);

            // Get products with min prices - Optimization: process multiple categories at once
            return minPrices.stream()
                .flatMap(minPrice -> productRepository.findProductsByCategoryIdAndPrice(
                        minPrice.categoryId(), minPrice.minPrice()).stream()
                        .limit(1)) // Get only the first product for each category
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            return List.of();
        }
    }

    /**
     * Find the cheapest products grouped by brand and category.
     * This is optimized for large datasets by implementing pagination.
     *
     * @return List of price information grouped by brand and category
     */
    @Cacheable(value = PRICE_INFO_CACHE, key = "'cheapestGroupByBrandCategory'")
    public List<BrandCategoryPriceInfo> findCheapestProductsGroupByBrandAndCategory() {
        try {
            // Direct query for small datasets
            long count = productRepository.count();
            if (count < 10000) { // Threshold setting
                return productRepository.findCheapestProductsGroupByBrandAndCategory();
            }
            
            // Pagination for large datasets
            List<BrandCategoryPriceInfo> results = new ArrayList<>();
            int pageNumber = 0;
            Page<BrandCategoryPriceInfo> page;
            
            do {
                Pageable pageable = PageRequest.of(pageNumber++, DEFAULT_PAGE_SIZE);
                page = productRepository.findCheapestProductsGroupByBrandAndCategoryPaged(pageable);
                results.addAll(page.getContent());
            } while (page.hasNext());
            
            return results;
        } catch (DataAccessException e) {
            return List.of();
        }
    }
    
    /**
     * Find the cheapest products for a specific brand across all categories.
     * More efficient when we only need data for one specific brand.
     *
     * @param brandId The brand ID to search
     * @return List of price information for the given brand across all categories
     */
    @Cacheable(value = PRICE_INFO_CACHE, key = "'cheapestByBrand:' + #brandId")
    public List<BrandCategoryPriceInfo> findCheapestProductsByBrand(Long brandId) {
        try {
            return productRepository.findCheapestProductsByBrand(brandId);
        } catch (DataAccessException e) {
            return List.of();
        }
    }

    /**
     * Find the cheapest products by category name.
     * This is optimized by first getting the minimum price, then fetching the products.
     *
     * @param categoryName The category name to search
     * @return List of products with the lowest price in the category
     */
    @Cacheable(value = PRICE_INFO_CACHE, key = "'cheapestByCategory:' + #categoryName")
    public List<Product> findCheapestByCategoryName(String categoryName) {
        try {
            BigDecimal minPrice = productRepository.findMinPriceByCategoryName(categoryName);
            if (minPrice == null) {
                return List.of();
            }

            return productRepository.findProductsByCategoryNameAndPrice(categoryName, minPrice);
        } catch (DataAccessException e) {
            return List.of();
        }
    }

    /**
     * Find the most expensive products by category name.
     * This is optimized by first getting the maximum price, then fetching the products.
     *
     * @param categoryName The category name to search
     * @return List of products with the highest price in the category
     */
    @Cacheable(value = PRICE_INFO_CACHE, key = "'expensiveByCategory:' + #categoryName")
    public List<Product> findMostExpensiveByCategoryName(String categoryName) {
        try {
            BigDecimal maxPrice = productRepository.findMaxPriceByCategoryName(categoryName);
            if (maxPrice == null) {
                return List.of();
            }

            return productRepository.findProductsByCategoryNameAndPrice(categoryName, maxPrice);
        } catch (DataAccessException e) {
            return List.of();
        }
    }
    
    /**
     * Cache invalidation method
     * Called when product data changes to invalidate related caches.
     */
    @CacheEvict(value = PRICE_INFO_CACHE, allEntries = true)
    public void clearPriceCache() {
    }
}
