package com.example.backend_tmdt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Vui lòng nhập tài khoản (Username/Email/SĐT)")
    private String loginKey;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(max = 50, message = "Mật khẩu quá dài")
    private String password;
}
