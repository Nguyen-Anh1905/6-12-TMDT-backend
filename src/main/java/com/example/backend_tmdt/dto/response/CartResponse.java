package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private long totalAmount;
    private int totalItems;
}
