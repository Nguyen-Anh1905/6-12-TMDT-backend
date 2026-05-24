package com.example.backend_tmdt.dto.response;

import lombok.*;

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
}
