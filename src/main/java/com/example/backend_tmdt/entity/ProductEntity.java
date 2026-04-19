package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name ="description", columnDefinition = "TEXT")
    private String description;

    @Column(name ="price", nullable = false)
    private Long price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "attributes", columnDefinition = "JSON")
    private String attributes;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 1")
    @Builder.Default
    private Integer status = 1; // 1: visible, 0: hidden

    @Column(name = "average_rating", columnDefinition = "FLOAT DEFAULT 0")
    @Builder.Default
    private Float averageRating = 0F;

    @Column(name = "sales_count", columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer salesCount = 0;

    @Column(name = "is_approved", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private Boolean isApproved = false; // For admin moderation

    // Product - Category (N - 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    // Product - Shop (N - 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop;

    // Product -> Reviews
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    // Product - cartProduct (1 - N)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CartProductEntity> cartProducts;
}