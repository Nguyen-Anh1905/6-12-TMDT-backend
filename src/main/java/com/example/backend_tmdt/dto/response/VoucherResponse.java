package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    private Long voucherId;
    private String code;

    private String discountType;  // PERCENT, FIXED
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer isActive;      // 1: active, 0: inactive
    private Long shopId;
    private String shopName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
