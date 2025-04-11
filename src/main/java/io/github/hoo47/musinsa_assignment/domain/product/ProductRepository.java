package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " + "JOIN FETCH p.brand b " + "WHERE p.price = (SELECT MIN(p2.price) FROM Product p2 WHERE p2.category.id = p.category.id) " + "AND p.category.id IN :categoryIds " + "ORDER BY p.category.id ASC")
    List<Product> findCheapestProductsByCategory(@Param("categoryIds") List<Long> categoryIds);

    @Query("""
                SELECT new io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo(
                    p.brand.id, p.brand.name,
                    p.category.id, p.category.name,
                    p.price
                )
                FROM Product p
                WHERE p.price = (
                    SELECT MIN(p2.price)
                    FROM Product p2
                    WHERE p2.category.id = p.category.id
                    AND p2.brand.id = p.brand.id
                )
                ORDER BY p.brand.id, p.category.id
            """)
    List<BrandCategoryPriceInfo> findCheapestProductsGroupByBrandAndCategory();

    @Query("select p from Product p where p.category.name = :categoryName and p.price = (select min(pp.price) from Product pp where pp.category.name = :categoryName)")
    List<Product> findCheapestByCategoryName(@Param("categoryName") String categoryName);

    @Query("select p from Product p where p.category.name = :categoryName and p.price = (select max(pp.price) from Product pp where pp.category.name = :categoryName)")
    List<Product> findMostExpensiveByCategoryName(@Param("categoryName") String categoryName);

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
