package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerOrderResponse {
    private Long orderId;
    private String orderCode;
    private Integer status;
    private String statusLabel;
    private Long totalAmount;
    private String paymentMethod;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String buyerName;
    private LocalDateTime createdAt;
    private List<SellerOrderItemResponse> items;
}
