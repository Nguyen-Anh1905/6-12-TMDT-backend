package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.response.DashboardStatisticsResponse;
import com.example.backend_tmdt.entity.OrderEntity;
import com.example.backend_tmdt.entity.ProductEntity;
import com.example.backend_tmdt.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final VoucherRepository voucherRepository;

    /**
     * Lấy thống kê tổng quan của hệ thống (Dashboard)
     * @return DashboardStatisticsResponse
     */
    public DashboardStatisticsResponse getDashboardStatistics() {
        // Đếm users (status = 1 là active)
        long totalUsers = userRepository.findAll().stream()
                .filter(u -> u.getStatus() == 1)
                .count();

        // Đếm shops (status = 1 là active)
        long totalShops = shopRepository.findAll().stream()
                .filter(s -> s.getStatus() == 1)
                .count();

        // Đếm tổng sản phẩm
        long totalProducts = productRepository.count();

        // Đếm sản phẩm đã duyệt
        long approvedProducts = productRepository.findAll().stream()
                .filter(ProductEntity::getIsApproved)
                .count();

        // Đếm sản phẩm chưa duyệt
        long pendingProducts = productRepository.findAll().stream()
                .filter(p -> !p.getIsApproved())
                .count();

        // Đếm đơn hàng
        long totalOrders = orderRepository.count();

        // Tính tổng doanh thu
        long totalRevenue = orderRepository.findAll().stream()
                .mapToLong(OrderEntity::getTotalAmount)
                .sum();

        // Đếm đánh giá
        long totalReviews = reviewRepository.count();

        // Tính giá trị đơn hàng trung bình
        double averageOrderValue = totalOrders > 0 ? (double) totalRevenue / totalOrders : 0;

        // Đếm voucher còn hoạt động (isActive = 1)
        long activeVouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getIsActive() == 1)
                .count();

        return DashboardStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalShops(totalShops)
                .totalProducts(totalProducts)
                .approvedProducts(approvedProducts)
                .pendingProducts(pendingProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalReviews(totalReviews)
                .averageOrderValue(averageOrderValue)
                .activeVouchers(activeVouchers)
                .build();
    }
}
