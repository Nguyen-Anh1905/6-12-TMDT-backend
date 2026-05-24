package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {
    private Long orderId;
    private Long productId;
    private Integer star;
    private String content;
}
