package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetailEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_at_purchase")
    private Integer priceAtPurchase;

    // orderDetail - order (N-1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // OrderDetail - product (N-1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
