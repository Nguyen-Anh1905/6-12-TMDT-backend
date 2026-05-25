package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.*;
import com.example.backend_tmdt.dto.response.*;
import com.example.backend_tmdt.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        return ResponseEntity.ok(ApiResponse.success(buyerService.getCart(), "Lay gio hang thanh cong"));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(buyerService.addToCart(request), "Them vao gio thanh cong"));
    }

    @PutMapping("/cart/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.updateCartItem(cartItemId, request), "Cap nhat gio hang thanh cong"));
    }

    @DeleteMapping("/cart/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(@PathVariable Long cartItemId) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.removeCartItem(cartItemId), "Xoa khoi gio thanh cong"));
    }

    @PostMapping("/cart/merge")
    public ResponseEntity<ApiResponse<CartResponse>> mergeGuestCart(@RequestBody MergeGuestCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.mergeGuestCart(request), "Dong bo gio hang thanh cong"));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<java.util.List<AddressResponse>>> getAddresses() {
        return ResponseEntity.ok(ApiResponse.success(buyerService.getAddresses(), "Lay danh sach dia chi thanh cong"));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(buyerService.createAddress(request), "Them dia chi thanh cong"));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.updateAddress(addressId, request), "Cap nhat dia chi thanh cong"));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long addressId) {
        buyerService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success("Xoa dia chi thanh cong"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<BuyerOrderResponse>> checkout(@RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(buyerService.checkout(request), "Dat hang thanh cong"));
    }

    @PostMapping("/vouchers/preview")
    public ResponseEntity<ApiResponse<VoucherPreviewResponse>> previewVoucher(@RequestBody VoucherPreviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.previewVoucher(request), "Preview voucher thanh cong"));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<BuyerOrderResponse>>> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.getOrders(status, page, pageSize), "Lay don hang thanh cong"));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<BuyerOrderResponse>> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.getOrderDetail(orderId), "Lay chi tiet don hang thanh cong"));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<BuyerOrderResponse>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(buyerService.cancelOrder(orderId), "Huy don hang thanh cong"));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createReview(@RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(buyerService.createReview(request), "Danh gia thanh cong"));
    }
}
