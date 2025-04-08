package io.github.hoo47.musinsa_assignment.domain.brand;

import io.github.hoo47.musinsa_assignment.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder
    public Brand(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
} 
