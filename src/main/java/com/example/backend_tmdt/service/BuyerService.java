package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.*;
import com.example.backend_tmdt.dto.response.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BuyerService {

    CartResponse getCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long cartItemId);

    CartResponse mergeGuestCart(MergeGuestCartRequest request);

    List<AddressResponse> getAddresses();

    AddressResponse createAddress(AddressRequest request);

    AddressResponse updateAddress(Long addressId, AddressRequest request);

    void deleteAddress(Long addressId);

    VoucherPreviewResponse previewVoucher(VoucherPreviewRequest request);

    BuyerOrderResponse checkout(CheckoutRequest request);

    Page<BuyerOrderResponse> getOrders(Integer status, int page, int pageSize);

    BuyerOrderResponse getOrderDetail(Long orderId);

    BuyerOrderResponse cancelOrder(Long orderId);

    ProductReviewResponse createReview(CreateReviewRequest request);
}
