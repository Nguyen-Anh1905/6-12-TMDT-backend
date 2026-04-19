package com.example.backend_tmdt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopProfileResponse {
    private Long shopId;
    private String shopName;
    private String description;
    private Integer status;
    private Float averageRating;
    private Integer productCount;
}
