package com.example.backend_tmdt.service.impl;

import com.example.backend_tmdt.constant.OrderStatus;
import com.example.backend_tmdt.dto.request.*;
import com.example.backend_tmdt.dto.response.*;
import com.example.backend_tmdt.entity.*;
import com.example.backend_tmdt.repository.*;
import com.example.backend_tmdt.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

    private static final long SHIPPING_FEE = 30000L;

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final PaymentRepository paymentRepository;
    private final VoucherRepository voucherRepository;
    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;

    private UserEntity requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Ban can dang nhap");
        }
        return userRepository.findByLoginKey(auth.getName())
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung"));
    }

    private CartEntity getOrCreateCart(UserEntity user) {
        return cartRepository.findByUserUserId(user.getUserId())
                .orElseGet(() -> cartRepository.save(CartEntity.builder().user(user).build()));
    }

    @Override
    public CartResponse getCart() {
        UserEntity user = requireUser();
        CartEntity cart = getOrCreateCart(user);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        UserEntity user = requireUser();
        if (request.getProductId() == null || request.getQuantity() == null || request.getQuantity() < 1) {
            throw new RuntimeException("Du lieu gio hang khong hop le");
        }

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("San pham khong ton tai"));

        if (product.getStatus() != 1 || !Boolean.TRUE.equals(product.getIsApproved())) {
            throw new RuntimeException("San pham khong con ban");
        }
        String variantLabel = request.getVariantLabel() != null ? request.getVariantLabel().trim() : null;
        int effectivePrice = request.getVariantPrice() != null ? request.getVariantPrice().intValue() : product.getPrice().intValue();

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Khong du ton kho");
        }

        CartEntity cart = getOrCreateCart(user);
        Optional<CartProductEntity> existing = cartProductRepository
                .findByCartCartIdAndProductProductIdAndVariantLabel(cart.getCartId(), product.getProductId(), variantLabel);

        if (existing.isPresent()) {
            CartProductEntity item = existing.get();
            int newQty = item.getQuantity() + request.getQuantity();
            if (newQty > product.getStockQuantity()) {
                throw new RuntimeException("Vuot qua ton kho");
            }
            item.setQuantity(newQty);
            item.setPrice(effectivePrice);
            item.setVariantLabel(variantLabel);
            item.setVariantPrice(request.getVariantPrice() != null ? request.getVariantPrice().intValue() : null);
            cartProductRepository.save(item);
        } else {
            cartProductRepository.save(CartProductEntity.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(effectivePrice)
                    .variantLabel(variantLabel)
                    .variantPrice(request.getVariantPrice() != null ? request.getVariantPrice().intValue() : null)
                    .build());
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request) {
        UserEntity user = requireUser();
        CartProductEntity item = cartProductRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("San pham trong gio khong ton tai"));

        if (!item.getCart().getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Khong co quyen");
        }
        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new RuntimeException("So luong khong hop le");
        }
        if (item.getProduct().getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Khong du ton kho");
        }

        item.setQuantity(request.getQuantity());
        cartProductRepository.save(item);
        return buildCartResponse(item.getCart());
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long cartItemId) {
        UserEntity user = requireUser();
        CartProductEntity item = cartProductRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("San pham trong gio khong ton tai"));

        if (!item.getCart().getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Khong co quyen");
        }

        CartEntity cart = item.getCart();
        cartProductRepository.delete(item);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse mergeGuestCart(MergeGuestCartRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return getCart();
        }
        for (MergeGuestCartRequest.GuestCartItemRequest item : request.getItems()) {
            if (item.getProductId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                try {
                    addToCart(AddToCartRequest.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .variantLabel(item.getVariantLabel())
                            .variantPrice(item.getVariantPrice())
                            .build());
                } catch (RuntimeException ignored) {
                    // Bo qua san pham het hang hoac khong hop le khi merge
                }
            }
        }
        return getCart();
    }

    @Override
    public List<AddressResponse> getAddresses() {
        UserEntity user = requireUser();
        return addressRepository.findByUserUserIdOrderByIsDefaultDescAddressIdDesc(user.getUserId())
                .stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        UserEntity user = requireUser();
        validateAddress(request);

        if (Integer.valueOf(1).equals(request.getIsDefault())) {
            clearDefaultAddress(user.getUserId());
        }

        AddressEntity address = AddressEntity.builder()
                .city(request.getCity().trim())
                .district(request.getDistrict().trim())
                .commune(request.getCommune().trim())
                .detail(request.getDetail().trim())
                .receiverName(request.getReceiverName().trim())
                .receiverPhone(request.getReceiverPhone().trim())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : 0)
                .user(user)
                .build();

        return toAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request) {
        UserEntity user = requireUser();
        AddressEntity address = addressRepository.findByAddressIdAndUserUserId(addressId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Dia chi khong ton tai"));

        if (request.getCity() != null) address.setCity(request.getCity().trim());
        if (request.getDistrict() != null) address.setDistrict(request.getDistrict().trim());
        if (request.getCommune() != null) address.setCommune(request.getCommune().trim());
        if (request.getDetail() != null) address.setDetail(request.getDetail().trim());
        if (request.getReceiverName() != null) address.setReceiverName(request.getReceiverName().trim());
        if (request.getReceiverPhone() != null) address.setReceiverPhone(request.getReceiverPhone().trim());
        if (request.getIsDefault() != null) {
            if (Integer.valueOf(1).equals(request.getIsDefault())) {
                clearDefaultAddress(user.getUserId());
            }
            address.setIsDefault(request.getIsDefault());
        }

        return toAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        UserEntity user = requireUser();
        AddressEntity address = addressRepository.findByAddressIdAndUserUserId(addressId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Dia chi khong ton tai"));
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public BuyerOrderResponse checkout(CheckoutRequest request) {
        UserEntity user = requireUser();
        if (request.getShopId() == null || request.getAddressId() == null) {
            throw new RuntimeException("Thieu thong tin dat hang");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
            throw new RuntimeException("Vui long chon phuong thuc thanh toan");
        }


        AddressEntity address = addressRepository.findByAddressIdAndUserUserId(request.getAddressId(), user.getUserId())
                .orElseThrow(() -> new RuntimeException("Dia chi khong ton tai"));

        CartEntity cart = getOrCreateCart(user);
    Set<Long> selectedIds = request.getSelectedCartItemIds() != null
        ? request.getSelectedCartItemIds().stream().filter(Objects::nonNull).collect(Collectors.toSet())
        : Collections.emptySet();

    List<CartProductEntity> shopItems = cartProductRepository.findByCartCartId(cart.getCartId()).stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getShop() != null
            && item.getProduct().getShop().getShopId().equals(request.getShopId())
            && (selectedIds.isEmpty() || selectedIds.contains(item.getId())))
                .collect(Collectors.toList());

        if (shopItems.isEmpty()) {
            throw new RuntimeException("Gio hang khong co san pham cua shop nay");
        }

        long subtotal = 0;
        for (CartProductEntity item : shopItems) {
            ProductEntity product = item.getProduct();
            if (product.getStatus() != 1 || !Boolean.TRUE.equals(product.getIsApproved())) {
                throw new RuntimeException("San pham " + product.getProductName() + " khong con ban");
            }
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("San pham " + product.getProductName() + " khong du ton kho");
            }
            subtotal += (long) item.getPrice() * item.getQuantity();
        }

        VoucherEntity voucher = null;
        long discount = 0;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            voucher = voucherRepository.findByCodeAndShopShopId(
                    request.getVoucherCode().trim().toUpperCase(), request.getShopId()
            ).orElseThrow(() -> new RuntimeException("Ma voucher khong hop le"));

            if (voucher.getIsActive() == null || voucher.getIsActive() != 1) {
                throw new RuntimeException("Voucher khong con hieu luc");
            }
            if (voucher.getMinOrderValue() != null && subtotal < voucher.getMinOrderValue().longValue()) {
                throw new RuntimeException("Don hang chua dat gia tri toi thieu");
            }
            // Enforce usage limit if configured
            Integer usageLimit = voucher.getUsageLimit();
            Integer usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
            if (usageLimit != null && usageLimit > 0 && usedCount >= usageLimit) {
                throw new RuntimeException("Voucher da het luong su dung");
            }
            discount = calculateDiscount(voucher, subtotal);
        }

        long totalAmount = Math.max(0, subtotal - discount) + SHIPPING_FEE;
        ShopEntity shop = shopItems.get(0).getProduct().getShop();

        String orderCode = "ORD-" + System.currentTimeMillis();
        OrderEntity order = OrderEntity.builder()
                .orderCode(orderCode)
                .shop(shop)
                .user(user)
                .shippingName(address.getReceiverName())
                .shippingPhone(address.getReceiverPhone())
                .shippingAddress(formatAddress(address))
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .voucher(voucher)
                .build();

        order = orderRepository.save(order);

        for (CartProductEntity item : shopItems) {
            ProductEntity product = item.getProduct();
            orderDetailRepository.save(OrderDetailEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .priceAtPurchase(item.getPrice())
                    .variantLabel(item.getVariantLabel())
                    .variantPrice(item.getVariantPrice())
                    .build());

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            product.setSalesCount((product.getSalesCount() != null ? product.getSalesCount() : 0) + item.getQuantity());
            productRepository.save(product);

            cartProductRepository.delete(item);
        }

        if (voucher != null) {
            voucher.setUsedCount((voucher.getUsedCount() != null ? voucher.getUsedCount() : 0) + 1);
            voucherRepository.save(voucher);
        }

        paymentRepository.save(PaymentEntity.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .transactionNo("TXN-" + orderCode)
                .amount((int) totalAmount)
                .status("COD".equalsIgnoreCase(request.getPaymentMethod()) ? 1 : 0)
                .build());

        orderTrackingRepository.save(OrderTrackingEntity.builder()
                .order(order)
                .status("CREATED")
                .location(address.getCity())
                .build());

        return toBuyerOrderResponse(order);
    }

    @Override
    public Page<BuyerOrderResponse> getOrders(Integer status, int page, int pageSize) {
        UserEntity user = requireUser();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<OrderEntity> orders = status != null
                ? orderRepository.findByUserUserIdAndStatus(user.getUserId(), status, pageable)
                : orderRepository.findByUserUserId(user.getUserId(), pageable);
        return orders.map(this::toBuyerOrderResponse);
    }

    @Override
    public BuyerOrderResponse getOrderDetail(Long orderId) {
        UserEntity user = requireUser();
        OrderEntity order = orderRepository.findByOrderIdAndUserUserId(orderId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Don hang khong ton tai"));
        return toBuyerOrderResponse(order);
    }

    @Override
    @Transactional
    public BuyerOrderResponse cancelOrder(Long orderId) {
        UserEntity user = requireUser();
        OrderEntity order = orderRepository.findByOrderIdAndUserUserId(orderId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Don hang khong ton tai"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chi huy don khi shop chua xac nhan");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        restoreOrderStock(orderId);

        orderTrackingRepository.save(OrderTrackingEntity.builder()
                .order(order)
                .status("CANCELLED")
                .location("He thong")
                .build());

        return toBuyerOrderResponse(order);
    }

    @Override
    @Transactional
    public ProductReviewResponse createReview(CreateReviewRequest request) {
        UserEntity user = requireUser();
        if (request.getStar() == null || request.getStar() < 1 || request.getStar() > 5) {
            throw new RuntimeException("Diem danh gia tu 1 den 5 sao");
        }

        OrderEntity order = orderRepository.findByOrderIdAndUserUserId(request.getOrderId(), user.getUserId())
                .orElseThrow(() -> new RuntimeException("Don hang khong ton tai"));

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("Chi danh gia sau khi nhan hang");
        }

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("San pham khong ton tai"));

        boolean productInOrder = orderDetailRepository.findByOrderOrderId(order.getOrderId()).stream()
            .anyMatch(detail -> detail.getProduct() != null
                && detail.getProduct().getProductId() != null
                && detail.getProduct().getProductId().equals(product.getProductId()));
        if (!productInOrder) {
            throw new RuntimeException("San pham khong thuoc don hang nay");
        }

        if (reviewRepository.existsByUserUserIdAndProductProductIdAndOrderOrderId(
                user.getUserId(), product.getProductId(), order.getOrderId())) {
            throw new RuntimeException("Ban da danh gia san pham nay");
        }

        ReviewEntity review = ReviewEntity.builder()
                .star(request.getStar())
                .content(request.getContent())
                .user(user)
                .product(product)
                .order(order)
                .build();

        reviewRepository.save(review);
        updateProductRating(product.getProductId());

        return ProductReviewResponse.builder()
                .reviewId(review.getReviewId())
                .buyerName(user.getFullName())
                .star(review.getStar())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private void updateProductRating(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProductProductIdOrderByCreatedAtDesc(productId);
        if (reviews.isEmpty()) return;
        double avg = reviews.stream().mapToInt(ReviewEntity::getStar).average().orElse(0);
        productRepository.findById(productId).ifPresent(p -> {
            p.setAverageRating((float) avg);
            productRepository.save(p);
        });
    }

    @Override
    public VoucherPreviewResponse previewVoucher(VoucherPreviewRequest request) {
        if (request == null || request.getShopId() == null || request.getSubtotal() == null) {
            throw new RuntimeException("Thieu thong tin preview voucher");
        }
        long subtotal = request.getSubtotal();
        if (request.getVoucherCode() == null || request.getVoucherCode().isBlank()) {
            return VoucherPreviewResponse.builder()
                    .subtotal(subtotal)
                    .discountAmount(0L)
                    .finalAmount(subtotal)
                    .shippingFee(SHIPPING_FEE)
                    .totalPayable(subtotal + SHIPPING_FEE)
                    .valid(true)
                    .message("Khong co voucher")
                    .build();
        }

        VoucherEntity voucher = voucherRepository.findByCodeAndShopShopId(
                request.getVoucherCode().trim().toUpperCase(), request.getShopId()
        ).orElseThrow(() -> new RuntimeException("Ma voucher khong hop le"));

        Integer usageLimit = voucher.getUsageLimit();
        Integer usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
        if (voucher.getIsActive() == null || voucher.getIsActive() != 1) {
            return buildVoucherPreviewResponse(voucher, subtotal, 0L, false, "Voucher khong con hieu luc", usageLimit, usedCount);
        }
        if (voucher.getMinOrderValue() != null && subtotal < voucher.getMinOrderValue().longValue()) {
            return buildVoucherPreviewResponse(voucher, subtotal, 0L, false, "Don hang chua dat gia tri toi thieu", usageLimit, usedCount);
        }
        if (usageLimit != null && usageLimit > 0 && usedCount >= usageLimit) {
            return buildVoucherPreviewResponse(voucher, subtotal, 0L, false, "Voucher da het luong su dung", usageLimit, usedCount);
        }

        long discount = calculateDiscount(voucher, subtotal);
        return buildVoucherPreviewResponse(voucher, subtotal, discount, true, "Ap dung voucher thanh cong", usageLimit, usedCount);
    }

    private VoucherPreviewResponse buildVoucherPreviewResponse(
            VoucherEntity voucher,
            long subtotal,
            long discount,
            boolean valid,
            String message,
            Integer usageLimit,
            Integer usedCount
    ) {
        return VoucherPreviewResponse.builder()
                .voucherId(voucher.getVoucherId())
                .code(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .maxDiscount(voucher.getMaxDiscount())
                .subtotal(subtotal)
                .discountAmount(discount)
                .finalAmount(Math.max(0L, subtotal - discount))
                .shippingFee(SHIPPING_FEE)
                .totalPayable(Math.max(0L, subtotal - discount) + SHIPPING_FEE)
                .usageLimit(usageLimit)
                .usedCount(usedCount)
                .isActive(voucher.getIsActive())
                .valid(valid)
                .message(message)
                .build();
    }

    private long calculateDiscount(VoucherEntity voucher, long subtotal) {
        long discount;
        if ("PERCENT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = (long) Math.floor(subtotal * voucher.getDiscountValue() / 100.0);
        } else {
            discount = voucher.getDiscountValue() != null ? voucher.getDiscountValue().longValue() : 0L;
        }

        if (voucher.getMaxDiscount() != null && voucher.getMaxDiscount() > 0) {
            discount = Math.min(discount, voucher.getMaxDiscount().longValue());
        }

        return Math.max(0L, discount);
    }

    private void restoreOrderStock(Long orderId) {
        List<OrderDetailEntity> details = orderDetailRepository.findByOrderOrderId(orderId);
        for (OrderDetailEntity detail : details) {
            ProductEntity product = detail.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + detail.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private void clearDefaultAddress(Long userId) {
        addressRepository.findByUserUserIdOrderByIsDefaultDescAddressIdDesc(userId).forEach(a -> {
            a.setIsDefault(0);
            addressRepository.save(a);
        });
    }

    private void validateAddress(AddressRequest request) {
        if (request.getCity() == null || request.getDistrict() == null
                || request.getCommune() == null || request.getDetail() == null
                || request.getReceiverName() == null || request.getReceiverPhone() == null) {
            throw new RuntimeException("Vui long nhap day du dia chi");
        }
    }

    private String formatAddress(AddressEntity address) {
        return String.join(", ",
                address.getDetail(),
                address.getCommune(),
                address.getDistrict(),
                address.getCity());
    }

    private CartResponse buildCartResponse(CartEntity cart) {
        List<CartProductEntity> items = cartProductRepository.findByCartCartId(cart.getCartId());
        List<CartItemResponse> itemResponses = items.stream().map(this::toCartItemResponse).collect(Collectors.toList());
        long total = itemResponses.stream().mapToLong(CartItemResponse::getLineTotal).sum();

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .items(itemResponses)
                .totalAmount(total)
                .totalItems(itemResponses.stream().mapToInt(CartItemResponse::getQuantity).sum())
                .build();
    }

    private CartItemResponse toCartItemResponse(CartProductEntity item) {
        ProductEntity product = item.getProduct();
        long price = item.getPrice() != null ? item.getPrice().longValue() : 0;
        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .price(price)
            .variantLabel(item.getVariantLabel())
                .quantity(item.getQuantity())
                .lineTotal(price * item.getQuantity())
                .shopId(product.getShop() != null ? product.getShop().getShopId() : null)
                .shopName(product.getShop() != null ? product.getShop().getShopName() : null)
                .stockQuantity(product.getStockQuantity())
                .build();
    }

    private AddressResponse toAddressResponse(AddressEntity address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .city(address.getCity())
                .district(address.getDistrict())
                .commune(address.getCommune())
                .detail(address.getDetail())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .isDefault(address.getIsDefault())
                .fullAddress(formatAddress(address))
                .build();
    }

    private BuyerOrderResponse toBuyerOrderResponse(OrderEntity order) {
        List<OrderDetailEntity> details = orderDetailRepository.findByOrderOrderId(order.getOrderId());
        Long userId = order.getUser() != null ? order.getUser().getUserId() : null;
        final Map<Long, ReviewEntity> reviewByProductId = userId == null
            ? Collections.emptyMap()
            : reviewRepository.findByOrderOrderIdAndUserUserId(order.getOrderId(), userId).stream()
                .filter(r -> r.getProduct() != null && r.getProduct().getProductId() != null)
                .collect(Collectors.toMap(
                    r -> r.getProduct().getProductId(),
                    r -> r,
                    (existing, replacement) -> existing.getCreatedAt() != null
                        && replacement.getCreatedAt() != null
                        && existing.getCreatedAt().isAfter(replacement.getCreatedAt())
                        ? existing
                        : replacement
                ));

        List<SellerOrderItemResponse> items = details.stream()
            .map(d -> {
                Long productId = d.getProduct() != null ? d.getProduct().getProductId() : null;
                ReviewEntity review = productId != null ? reviewByProductId.get(productId) : null;
                return SellerOrderItemResponse.builder()
                    .productId(productId)
                    .productName(d.getProduct() != null ? d.getProduct().getProductName() : null)
                    .imageUrl(d.getProduct() != null ? d.getProduct().getImageUrl() : null)
                    .quantity(d.getQuantity())
                    .priceAtPurchase(d.getPriceAtPurchase() != null ? d.getPriceAtPurchase().longValue() : 0L)
                    .variantLabel(d.getVariantLabel())
                    .lineTotal((d.getPriceAtPurchase() != null ? d.getPriceAtPurchase().longValue() : 0L) * d.getQuantity())
                    .reviewId(review != null ? review.getReviewId() : null)
                    .reviewStar(review != null ? review.getStar() : null)
                    .reviewContent(review != null ? review.getContent() : null)
                    .reviewCreatedAt(review != null ? review.getCreatedAt() : null)
                    .build();
            })
                .collect(Collectors.toList());

        long subtotal = items.stream().mapToLong(i -> i.getLineTotal() != null ? i.getLineTotal() : 0L).sum();
        long orderTotal = order.getTotalAmount() != null ? order.getTotalAmount() : 0L;
        long discount = Math.max(0, subtotal + SHIPPING_FEE - orderTotal);

        return BuyerOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus())
                .statusLabel(OrderStatus.label(order.getStatus()))
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress())
                .shopId(order.getShop() != null ? order.getShop().getShopId() : null)
                .shopName(order.getShop() != null ? order.getShop().getShopName() : null)
                .createdAt(order.getCreatedAt())
            .items(items)
            .subtotal(subtotal)
            .shippingFee(SHIPPING_FEE)
            .discountAmount(discount)
            .voucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null)
                .build();
    }
}
