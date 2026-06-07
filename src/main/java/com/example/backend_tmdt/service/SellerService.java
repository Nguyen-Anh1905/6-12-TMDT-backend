package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.*;
import com.example.backend_tmdt.dto.response.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface SellerService {

    SellerDashboardResponse getDashboard();

    ShopProfileResponse getShopProfile();

    ShopProfileResponse updateShopProfile(UpdateShopRequest request);

    Page<ProductResponse> getProducts(int page, int pageSize);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    ProductResponse updateProductVisibility(Long productId, VisibilityRequest request);

    ProductResponse updatePriceQuantity(Long productId, UpdatePriceQuantityRequest request);

    void deleteProduct(Long productId);

    Page<SellerOrderResponse> getOrders(Integer status, int page, int pageSize);

    SellerOrderResponse getOrderDetail(Long orderId);

    SellerOrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    SellerRevenueResponse getRevenue(LocalDate from, LocalDate to);

    Page<SellerReviewResponse> getReviews(int page, int pageSize);

    SellerReviewResponse replyToReview(Long reviewId, CreateReplyRequest request);

    java.util.List<VoucherResponse> getVouchers();

    VoucherResponse createVoucher(CreateVoucherRequest request);

    VoucherResponse updateVoucher(Long voucherId, UpdateVoucherRequest request);

    void deleteVoucher(Long voucherId);

    SellerStatisticsResponse getAdvancedStatistics(LocalDate from, LocalDate to);
}
