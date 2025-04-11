package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import io.github.hoo47.musinsa_assignment.domain.product.dto.CategoryMinPrice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Get minimum prices for each category
     *
     * @param categoryIds list of category IDs
     * @return list of categories with their minimum prices
     */
    @Query("SELECT new io.github.hoo47.musinsa_assignment.domain.product.dto.CategoryMinPrice(" +
            "p.category.id, MIN(p.price)) " +
            "FROM Product p " +
            "WHERE p.category.id IN :categoryIds " +
            "GROUP BY p.category.id")
    List<CategoryMinPrice> findMinPricesByCategories(@Param("categoryIds") List<Long> categoryIds);

    /**
     * Find products with the cheapest price in each category
     *
     * @param categoryId category ID
     * @param price      minimum price for the category
     * @return list of products with the given price in the given category
     */
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.brand " +
            "JOIN FETCH p.category " +
            "WHERE p.category.id = :categoryId AND p.price = :price")
    List<Product> findProductsByCategoryIdAndPrice(@Param("categoryId") Long categoryId, @Param("price") BigDecimal price);

    /**
     * Find all products with minimum price per brand and category combination
     *
     * @return list of brand-category price info records
     */
    @Query("""
            SELECT new io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo(
                b.id, b.name,
                c.id, c.name,
                minPrices.minPrice
            )
            FROM (
                SELECT p.brand.id as brandId, p.category.id as categoryId, MIN(p.price) as minPrice
                FROM Product p
                GROUP BY p.brand.id, p.category.id
            ) AS minPrices
            JOIN Brand b ON b.id = minPrices.brandId
            JOIN Category c ON c.id = minPrices.categoryId
            ORDER BY b.id, c.id
            """)
    List<BrandCategoryPriceInfo> findCheapestProductsGroupByBrandAndCategory();

    /**
     * Get the minimum price for a category by name
     *
     * @param categoryName category name
     * @return minimum price for the category
     */
    @Query("SELECT MIN(p.price) FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    BigDecimal findMinPriceByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Get the maximum price for a category by name
     *
     * @param categoryName category name
     * @return maximum price for the category
     */
    @Query("SELECT MAX(p.price) FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    BigDecimal findMaxPriceByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Find products by category name and price
     *
     * @param categoryName category name
     * @param price        product price
     * @return list of products with the given price in the given category
     */
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.brand " +
            "JOIN FETCH p.category c " +
            "WHERE c.name = :categoryName AND p.price = :price")
    List<Product> findProductsByCategoryNameAndPrice(@Param("categoryName") String categoryName, @Param("price") BigDecimal price);

    /**
     * Retrieves a product by its ID with a pessimistic read lock.
     * Prevents concurrent modification of the product by other transactions.
     *
     * @param id the product ID
     * @return the product entity (Optional) with eagerly fetched brand and category
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT p FROM Product p JOIN FETCH p.brand JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithReadLock(@Param("id") Long id);
}
