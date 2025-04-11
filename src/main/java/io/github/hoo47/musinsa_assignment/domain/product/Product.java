package io.github.hoo47.musinsa_assignment.domain.product;

import io.github.hoo47.musinsa_assignment.domain.BaseTimeEntity;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "products")
@Builder
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateBrand(Brand brand) {
        this.brand = brand;
    }

    public void updatePrice(BigDecimal price) {
        this.price = price;
    }
} 
