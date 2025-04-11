package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import io.github.hoo47.musinsa_assignment.domain.product.dto.CategoryMinPrice;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
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
     * Optimized with pagination support for large datasets
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
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<BrandCategoryPriceInfo> findCheapestProductsGroupByBrandAndCategory();

    /**
     * Find all products with minimum price per brand and category combination with pagination
     * Optimized for large datasets by adding pagination support
     *
     * @param pageable pagination information
     * @return page of brand-category price info records
     */
    @Query(value = """
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
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM (
                        SELECT p.brand.id as brandId, p.category.id as categoryId
                        FROM Product p
                        GROUP BY p.brand.id, p.category.id
                    ) AS countQuery
                    """)
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page<BrandCategoryPriceInfo> findCheapestProductsGroupByBrandAndCategoryPaged(Pageable pageable);

    /**
     * Find products with minimum price per category for a specific brand
     * This method is more efficient when filtering by a specific brand
     *
     * @param brandId brand ID to filter by
     * @return list of brand-category price info records for the given brand
     */
    @Query("""
            SELECT new io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo(
                :brandId, b.name,
                c.id, c.name,
                MIN(p.price)
            )
            FROM Product p
            JOIN p.brand b
            JOIN p.category c
            WHERE p.brand.id = :brandId
            GROUP BY c.id, c.name, b.name
            ORDER BY c.id
            """)
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<BrandCategoryPriceInfo> findCheapestProductsByBrand(@Param("brandId") Long brandId);

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
