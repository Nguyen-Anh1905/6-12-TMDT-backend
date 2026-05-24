package com.example.backend_tmdt.dto.request;


import jakarta.validation.constraints.*;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UpdateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 50, message = "Mã voucher tối đa 50 ký tự")
    private String code;

    @NotBlank(message = "Loại giảm giá không được để trống")
    @Pattern(regexp = "^(PERCENT|FIXED)$", message = "Loại giảm giá phải là PERCENT hoặc FIXED")
    private String discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Positive(message = "Giá trị giảm phải > 0")
    private Double discountValue;

    @PositiveOrZero(message = "Giá tối thiểu phải >= 0")
    private Double minOrderValue;

    @PositiveOrZero(message = "Giảm tối đa phải >= 0")
    private Double maxDiscount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    @PositiveOrZero(message = "Giới hạn sử dụng phải >= 0")
    private Integer usageLimit;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái phải là 0 hoặc 1")
    @Max(value = 1, message = "Trạng thái phải là 0 hoặc 1")

    private Integer isActive;
}
