package com.example.backend_tmdt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Long price;
    private Integer quantity;
    private Long lineTotal;
    private Long shopId;
    private String shopName;
    private Integer stockQuantity;
}
