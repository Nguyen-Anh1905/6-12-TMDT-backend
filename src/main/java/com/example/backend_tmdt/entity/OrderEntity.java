package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_code", nullable = false, unique = true, length = 50)
    private String orderCode;

    @Column(name = "shipping_name", nullable = false)
    private String shippingName;

    @Column(name = "shipping_phone", nullable = false, length = 15)
    private String shippingPhone;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "status", nullable = false)
    private Integer status;

    // Order - voucher (N - 1)
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private VoucherEntity voucher;

    // order - user (N-1)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // order - orderDetail (1-N)
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetailEntity> orderDetails;

    //order - shop (N-1)
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop;

    // order - payment (1-1)
    @OneToOne(mappedBy = "order")
    private PaymentEntity payment;

    // Order - orderTracking (1 - N)
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderTrackingEntity> orderTrackings;

    // Order - review (1 - N)
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;
}