package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}
