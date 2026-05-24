package com.example.backend_tmdt.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductApprovalRequest {

    @NotBlank(message = "Quyết định không được để trống (approve/reject)")
    private String decision;  // "approve" hoặc "reject"

    private String reason;  // Lý do từ chối (nếu reject)
}
