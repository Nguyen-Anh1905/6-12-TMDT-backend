package com.example.backend_tmdt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long addressId;
    private String city;
    private String district;
    private String commune;
    private String detail;
    private String receiverName;
    private String receiverPhone;
    private Integer isDefault;
    private String fullAddress;
}
