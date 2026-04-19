package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    private String productName;
    private String description;
    private Long price;
    private Integer stockQuantity;
    private String imageUrl;
    private String attributes; // JSON format
    private Long categoryId;
    private Integer status; // 1: visible, 0: hidden
}
