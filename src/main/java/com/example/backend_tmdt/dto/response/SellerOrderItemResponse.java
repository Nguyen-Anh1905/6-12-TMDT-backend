package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerOrderItemResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private Long priceAtPurchase;
    private String variantLabel;
    private Long lineTotal;
    private Long reviewId;
    private Integer reviewStar;
    private String reviewContent;
    private LocalDateTime reviewCreatedAt;
}
