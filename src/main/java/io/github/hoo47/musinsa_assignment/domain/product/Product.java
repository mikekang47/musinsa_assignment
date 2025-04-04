package io.github.hoo47.musinsa_assignment.domain.product;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;

import io.github.hoo47.musinsa_assignment.domain.BaseTimeEntity;
import io.github.hoo47.musinsa_assignment.domain.category.Category;

@Entity
@Getter
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

    protected Product() { } // JPA no-args constructor

    public Product(BigDecimal price, Category category, String brand) {
        this.price = price;
        this.category = category;
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", price=" + price +
                ", category=" + (category != null ? category.getName() : null) +
                ", brand='" + brand + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", lastModifiedAt=" + getLastModifiedAt() +
                '}';
    }
} 