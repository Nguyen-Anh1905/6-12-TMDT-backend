package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.sql.results.graph.Fetch;

import java.util.List;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    private String description;

    @Column(nullable = false)
    private Integer status;

    // Shop - User (1 shop thuộc 1 user)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Shop - Products (1-N)
    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    // Shop - Vouchers (1-N)
    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<VoucherEntity> vouchers;

    // shop - order (1-N)
    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<OrderEntity> orders;

    // shop - reply (1 - n)
    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<ReplyEntity> replies;
}
