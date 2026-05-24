package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private String city;
    private String district;
    private String commune;
    private String detail;
    private String receiverName;
    private String receiverPhone;
    private Integer isDefault;
}
