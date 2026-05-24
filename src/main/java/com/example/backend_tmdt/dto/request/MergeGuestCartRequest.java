package com.example.backend_tmdt.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergeGuestCartRequest {
    private List<GuestCartItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GuestCartItemRequest {
        private Long productId;
        private Integer quantity;
        private String variantLabel;
        private Long variantPrice;
    }
}
