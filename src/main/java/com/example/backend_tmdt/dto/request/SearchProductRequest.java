package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProductRequest {
    private String keyword;
    private Long minPrice;
    private Long maxPrice;
    private Long categoryId;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer pageSize = 20;
}
