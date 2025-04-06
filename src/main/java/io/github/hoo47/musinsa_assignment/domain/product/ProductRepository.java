package io.github.hoo47.musinsa_assignment.domain.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
            SELECT p FROM Product p
            JOIN FETCH p.category c
            JOIN FETCH p.brand b
            WHERE c.id = :categoryId
            ORDER BY b.name
            """)
    List<Product> findByCategoryWithFetch(@Param("categoryId") Long categoryId);
}
