package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.LoginDTO;
import com.example.backend_tmdt.dto.request.RefreshTokenRequestDTO;
import com.example.backend_tmdt.dto.request.RegisterDTO;
import com.example.backend_tmdt.dto.response.LoginResponseDTO;
import com.example.backend_tmdt.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/register")
    public String register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    @PostMapping("/auth/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    // khi hết hạn access token thì gọi endpoint để refresh token
    @PostMapping("/auth/refresh-token")
    public LoginResponseDTO refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return authService.refreshToken(request.getRefreshToken());
    }

    @PostMapping("/auth/logout")
    public String logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return "Đăng xuất thành công!";
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPing(Authentication authentication) {
        return "ADMIN OK - " + authentication.getName();
    }

    @GetMapping("/seller/ping")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public String sellerPing(Authentication authentication) {
        return "SELLER OK - " + authentication.getName();
    }

    @GetMapping("/buyer/ping")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public String buyerPing(Authentication authentication) {
        return "BUYER OK - " + authentication.getName();
    }
}
