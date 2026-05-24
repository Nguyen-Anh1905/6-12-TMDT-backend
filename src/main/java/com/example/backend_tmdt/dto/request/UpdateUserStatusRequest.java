package com.example.backend_tmdt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private Integer status; // 1: hoạt động, 0: khóa
}
