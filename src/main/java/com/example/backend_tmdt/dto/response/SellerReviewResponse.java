package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerReviewResponse {
    private Long reviewId;
    private Long productId;
    private String productName;
    private String buyerName;
    private Integer star;
    private String content;
    private LocalDateTime createdAt;
    private Long replyId;
    private String replyContent;
    private LocalDateTime replyCreatedAt;
}
