package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_products")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CartProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Integer price;

    // CartProduct - cart (N-1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    // CartProduct - product (N-1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
