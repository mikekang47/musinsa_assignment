package io.github.hoo47.musinsa_assignment.domain.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findTop1ByCategoryIdOrderByPriceAsc(Long categoryId);
}
