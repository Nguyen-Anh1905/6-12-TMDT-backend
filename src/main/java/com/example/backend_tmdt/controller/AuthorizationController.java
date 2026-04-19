package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthorizationController {

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> adminPing(Authentication authentication) {
        return ApiResponse.success("ADMIN OK - " + authentication.getName(), "Truy cập ADMIN thành công");
    }

    @GetMapping("/seller/ping")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ApiResponse<String> sellerPing(Authentication authentication) {
        return ApiResponse.success("SELLER OK - " + authentication.getName(), "Truy cập SELLER thành công");
    }

    @GetMapping("/buyer/ping")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public ApiResponse<String> buyerPing(Authentication authentication) {
        return ApiResponse.success("BUYER OK - " + authentication.getName(), "Truy cập BUYER thành công");
    }
}
