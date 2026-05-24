package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerRevenueResponse {
    private long totalRevenue;
    private long totalOrders;
    private long completedOrders;
    private List<RevenueByDay> byDay;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueByDay {
        private String date;
        private long revenue;
        private long orderCount;
    }
}
