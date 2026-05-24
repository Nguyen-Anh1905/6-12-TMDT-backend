package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    private Long shopId;
    private Long addressId;
    private String paymentMethod;
    private String voucherCode;
}
