package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.DashboardStatisticsResponse;
import com.example.backend_tmdt.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    /**
     * Lấy thống kê tổng quan của hệ thống (Dashboard)
     * GET /api/admin/statistics/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatisticsResponse>> getDashboardStatistics() {
        DashboardStatisticsResponse response = adminStatisticsService.getDashboardStatistics();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thống kê dashboard thành công!"));
    }
}
