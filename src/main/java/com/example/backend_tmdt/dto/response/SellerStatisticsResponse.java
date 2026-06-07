package com.example.backend_tmdt.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerStatisticsResponse {
    private ActualRevenueInfo actualRevenue;
    private List<ProductStatistic> topSellingProducts;
    private List<ProductStatistic> topRevenueProducts;
    private RevenueComparison revenueComparison;
    private AverageMetrics averageMetrics;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductStatistic {
        private Long productId;
        private String productName;
        private String variantLabel;
        private String imageUrl;
        private long totalQuantitySold;
        private long totalRevenue;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActualRevenueInfo {
        private long grossRevenue;
        private long platformFee;
        private long voucherDiscount;
        private long netRevenue;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueComparison {
        private long currentPeriodRevenue;
        private long previousPeriodRevenue;
        private double growthRate; // Percentage
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AverageMetrics {
        private double averageOrderValue;
    }
}
