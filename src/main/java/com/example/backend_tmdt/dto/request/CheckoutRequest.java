package com.example.backend_tmdt.dto.request;

import lombok.*;
import java.util.List;

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
    private List<Long> selectedCartItemIds;
}
