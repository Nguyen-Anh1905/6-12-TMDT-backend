package com.example.backend_tmdt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Nếu data bị null, sẽ giấu luôn field "result" cho JSON gọn gàng
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;          // Mã trạng thái (1000 = OK, 9999 = Lỗi)
    private String message;    // Thông báo
    private T result;          // Dữ liệu trả về

    // Thành công và CÓ dữ liệu (ví dụ: Login trả về Token)
    public static <T> ApiResponse<T> success(T result, String message) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .result(result)
                .build();
    }

    // Thành công nhưng KHÔNG có dữ liệu (ví dụ: Đăng ký)
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .build();
    }

    // Thất bại
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
