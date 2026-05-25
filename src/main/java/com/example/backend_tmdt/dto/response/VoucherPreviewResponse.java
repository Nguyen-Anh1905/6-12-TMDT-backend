package com.example.backend_tmdt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherPreviewResponse {
    private Long voucherId;
    private String code;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscount;
    private Long subtotal;
    private Long discountAmount;
    private Long finalAmount;
    private Long shippingFee;
    private Long totalPayable;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer isActive;
    private boolean valid;
    private String message;
}