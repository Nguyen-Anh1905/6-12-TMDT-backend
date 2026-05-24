package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.*;
import com.example.backend_tmdt.dto.response.*;
import com.example.backend_tmdt.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<SellerDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getDashboard(), "Lay thong ke thanh cong"));
    }

    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ShopProfileResponse>> getShop() {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getShopProfile(), "Lay thong tin shop thanh cong"));
    }

    @PutMapping("/shop")
    public ResponseEntity<ApiResponse<ShopProfileResponse>> updateShop(@RequestBody UpdateShopRequest request) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updateShopProfile(request), "Cap nhat shop thanh cong"));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getProducts(page, pageSize), "Lay danh sach san pham thanh cong"));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request) {
        ProductResponse created = sellerService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Tao san pham thanh cong"));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updateProduct(productId, request), "Cap nhat san pham thanh cong"));
    }

    @PatchMapping("/products/{productId}/visibility")
    public ResponseEntity<ApiResponse<ProductResponse>> updateVisibility(
            @PathVariable Long productId,
            @RequestBody VisibilityRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updateProductVisibility(productId, request), "Cap nhat trang thai san pham thanh cong"));
    }

    @PatchMapping("/products/{productId}/price-quantity")
    public ResponseEntity<ApiResponse<ProductResponse>> updatePriceQuantity(
            @PathVariable Long productId,
            @RequestBody UpdatePriceQuantityRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updatePriceQuantity(productId, request), "Cap nhat gia va ton kho thanh cong"));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        sellerService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Xoa san pham thanh cong"));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<SellerOrderResponse>>> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getOrders(status, page, pageSize), "Lay danh sach don hang thanh cong"));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<SellerOrderResponse>> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getOrderDetail(orderId), "Lay chi tiet don hang thanh cong"));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<SellerOrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updateOrderStatus(orderId, request), "Cap nhat trang thai don hang thanh cong"));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<SellerRevenueResponse>> getRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getRevenue(from, to), "Lay thong ke doanh thu thanh cong"));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<Page<SellerReviewResponse>>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getReviews(page, pageSize), "Lay danh sach danh gia thanh cong"));
    }

    @PostMapping("/reviews/{reviewId}/reply")
    public ResponseEntity<ApiResponse<SellerReviewResponse>> replyReview(
            @PathVariable Long reviewId,
            @RequestBody CreateReplyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sellerService.replyToReview(reviewId, request), "Tra loi danh gia thanh cong"));
    }

    @GetMapping("/vouchers")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getVouchers() {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getVouchers(), "Lay danh sach voucher thanh cong"));
    }

    @PostMapping("/vouchers")
    public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(@RequestBody CreateVoucherRequest request) {
        VoucherResponse created = sellerService.createVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Tao voucher thanh cong"));
    }

    @PutMapping("/vouchers/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> updateVoucher(
            @PathVariable Long voucherId,
            @RequestBody UpdateVoucherRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updateVoucher(voucherId, request), "Cap nhat voucher thanh cong"));
    }

    @DeleteMapping("/vouchers/{voucherId}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable Long voucherId) {
        sellerService.deleteVoucher(voucherId);
        return ResponseEntity.ok(ApiResponse.success("Xoa voucher thanh cong"));
    }
}
