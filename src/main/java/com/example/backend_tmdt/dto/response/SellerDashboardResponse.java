package com.example.backend_tmdt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDashboardResponse {
    private Long shopId;
    private String shopName;
    private long totalProducts;
    private long activeProducts;
    private long pendingApprovalProducts;
    private long pendingOrders;
    private long shippingOrders;
    private long completedOrders;
    private long totalRevenue;
    private long todayRevenue;
}
