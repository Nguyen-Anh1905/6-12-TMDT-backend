package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.LoginDTO;
import com.example.backend_tmdt.dto.request.RefreshTokenRequestDTO;
import com.example.backend_tmdt.dto.request.RegisterDTO;
import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.LoginResponseDTO;
import com.example.backend_tmdt.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công!"));
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công!"));
    }

    // Làm mới access token
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        LoginResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Làm mới token thành công!"));
    }

    // Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công!"));
    }
}
