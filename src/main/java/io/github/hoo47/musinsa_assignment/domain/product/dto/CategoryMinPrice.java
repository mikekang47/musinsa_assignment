package io.github.hoo47.musinsa_assignment.domain.product.dto;

import java.math.BigDecimal;

/**
 * DTO for storing the minimum price for each category
 * 
 * @param categoryId the category ID
 * @param minPrice the minimum price for the category
 */
public record CategoryMinPrice(
    Long categoryId,
    BigDecimal minPrice
) {
} 