package com.example.backend_tmdt.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesRequest {

    @NotEmpty(message = "Danh sách role không được để trống")
    private List<String> roles; // VD: ["ROLE_BUYER", "ROLE_SELLER"]
}
