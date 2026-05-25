package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherPreviewRequest {
    private Long shopId;
    private String voucherCode;
    private Long subtotal;
}