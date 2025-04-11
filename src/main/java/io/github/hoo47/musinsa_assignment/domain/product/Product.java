package io.github.hoo47.musinsa_assignment.domain.product;

import java.math.BigDecimal;

import io.github.hoo47.musinsa_assignment.domain.BaseTimeEntity;
import io.github.hoo47.musinsa_assignment.domain.brand.Brand;
import io.github.hoo47.musinsa_assignment.domain.category.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_category_price", columnList = "category_id, price"),
        @Index(name = "idx_product_brand_category", columnList = "brand_id, category_id")
    }
)
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
