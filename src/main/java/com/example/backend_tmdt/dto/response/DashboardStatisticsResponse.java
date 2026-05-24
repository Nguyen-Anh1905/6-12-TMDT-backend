package com.example.backend_tmdt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatisticsResponse {
    private Long totalUsers;
    private Long totalShops;
    private Long totalProducts;
    private Long approvedProducts;
    private Long pendingProducts;
    private Long totalOrders;
    private Long totalRevenue;
    private Long totalReviews;
    private Double averageOrderValue;
    private Long activeVouchers;
}
