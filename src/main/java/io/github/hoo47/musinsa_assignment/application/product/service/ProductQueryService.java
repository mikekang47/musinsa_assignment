package io.github.hoo47.musinsa_assignment.application.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Find the cheapest product in each category.
     * This method is optimized to reduce database load by executing two separate queries:
     * 1. First, get the minimum price for each category
     * 2. Then, fetch each product with the minimum price
     *
     * @param categoryIds List of category IDs to search
     * @return List of products with the lowest price in each category
     */
    public List<Product> getCheapestProductInCategory(List<Long> categoryIds) {
        // Get min prices for each category
        List<CategoryMinPrice> minPrices = productRepository.findMinPricesByCategories(categoryIds);
        
        // Get products with min prices
        List<Product> result = new ArrayList<>();
        for (CategoryMinPrice minPrice : minPrices) {
            // Fetch the first product with the min price for each category
            List<Product> products = productRepository.findProductsByCategoryIdAndPrice(
                    minPrice.categoryId(), minPrice.minPrice());
            if (!products.isEmpty()) {
                result.add(products.get(0));
            }
        }
        
        return result;
    }

    /**
     * Find the cheapest products grouped by brand and category.
     * This is optimized by using a derived table in the query.
     *
     * @return List of price information grouped by brand and category
     */
    public List<BrandCategoryPriceInfo> findCheapestProductsGroupByBrandAndCategory() {
        return productRepository.findCheapestProductsGroupByBrandAndCategory();
    }

    /**
     * Find the cheapest products by category name.
     * This is optimized by first getting the minimum price, then fetching the products.
     *
     * @param categoryName The category name to search
     * @return List of products with the lowest price in the category
     */
    public List<Product> findCheapestByCategoryName(String categoryName) {
        BigDecimal minPrice = productRepository.findMinPriceByCategoryName(categoryName);
        if (minPrice == null) {
            return List.of();
        }
        
        return productRepository.findProductsByCategoryNameAndPrice(categoryName, minPrice);
    }

    /**
     * Find the most expensive products by category name.
     * This is optimized by first getting the maximum price, then fetching the products.
     *
     * @param categoryName The category name to search
     * @return List of products with the highest price in the category
     */
    public List<Product> findMostExpensiveByCategoryName(String categoryName) {
        BigDecimal maxPrice = productRepository.findMaxPriceByCategoryName(categoryName);
        if (maxPrice == null) {
            return List.of();
        }
        
        return productRepository.findProductsByCategoryNameAndPrice(categoryName, maxPrice);
    }
}
