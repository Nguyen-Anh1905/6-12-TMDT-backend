package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "discount_type", nullable = false)
    private String discountType;
    // PERCENT hoặc FIXED

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "is_active")
    private Integer isActive;

    // voucher - shop (N - 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop;

    // voucher - order (1 - N)
    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
    private List<OrderEntity> orders;
}