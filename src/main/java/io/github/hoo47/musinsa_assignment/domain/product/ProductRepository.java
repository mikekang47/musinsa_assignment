package io.github.hoo47.musinsa_assignment.domain.product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.brand b " +
            "WHERE p.price = (SELECT MIN(p2.price) FROM Product p2 WHERE p2.category.id = p.category.id) " +
            "AND p.category.id IN :categoryIds " +
            "ORDER BY p.category.id ASC")
    List<Product> findCheapestProductsByCategory(@Param("categoryIds") List<Long> categoryIds);
}
