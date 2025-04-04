package io.github.hoo47.musinsa_assignment.domain.product;

import java.math.BigDecimal;

import io.github.hoo47.musinsa_assignment.domain.BaseTimeEntity;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "products")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String brand;

    @Builder
    public Product(BigDecimal price, Category category, String brand) {
        this.price = price;
        this.category = category;
        this.brand = brand;
    }
} 
