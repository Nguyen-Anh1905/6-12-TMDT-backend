package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewResponse {
    private Long reviewId;
    private String buyerName;
    private Integer star;
    private String content;
    private LocalDateTime createdAt;
    private String shopReply;
}
