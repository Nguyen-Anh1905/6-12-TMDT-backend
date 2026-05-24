package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private Long price;
    private Integer stockQuantity;
    private String imageUrl;
    private Integer status;
    private Boolean isApproved;
    private Long shopId;
    private String shopName;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
