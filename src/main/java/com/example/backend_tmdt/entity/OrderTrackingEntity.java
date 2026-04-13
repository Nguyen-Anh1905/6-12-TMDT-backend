package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_trackings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OrderTrackingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "location")
    private String location;

    // OrderTracking - order (N-1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
}
