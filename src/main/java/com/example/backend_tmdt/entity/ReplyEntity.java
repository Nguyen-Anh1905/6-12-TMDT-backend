package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "replies")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ReplyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    // reply - review ( n - 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    // reply - shop (n - 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop;
}
