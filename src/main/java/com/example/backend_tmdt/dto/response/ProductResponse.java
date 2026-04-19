package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private Long price;
    private Integer stockQuantity;
    private String imageUrl;
    private String attributes;
    private Integer status;
    private Float averageRating;
    private Integer salesCount;
    private Boolean isApproved;
    private Long categoryId;
    private String categoryName;
    private Long shopId;
    private String shopName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
