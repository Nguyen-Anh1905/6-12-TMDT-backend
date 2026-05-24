package com.example.backend_tmdt.dto.request;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVoucherRequest {
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscount;
    private Integer usageLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer isActive;
}
