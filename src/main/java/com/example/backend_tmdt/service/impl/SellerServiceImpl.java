package com.example.backend_tmdt.service.impl;

import com.example.backend_tmdt.constant.OrderStatus;
import com.example.backend_tmdt.dto.request.*;
import com.example.backend_tmdt.dto.response.*;
import com.example.backend_tmdt.entity.*;
import com.example.backend_tmdt.mapper.ProductMapper;
import com.example.backend_tmdt.repository.*;
import com.example.backend_tmdt.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private static final Set<Integer> REVENUE_STATUSES = Set.of(OrderStatus.DELIVERED, OrderStatus.COMPLETED);

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;
    private final VoucherRepository voucherRepository;
    private final ProductMapper productMapper;

    private ShopEntity requireShop() {
        UserEntity user = getCurrentUser();
        if (user == null || user.getShop() == null) {
            throw new RuntimeException("Tai khoan seller chua co gian hang");
        }
        return user.getShop();
    }

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByLoginKey(authentication.getName()).orElse(null);
    }

    @Override
    public SellerDashboardResponse getDashboard() {
        ShopEntity shop = requireShop();
        Long shopId = shop.getShopId();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return SellerDashboardResponse.builder()
                .shopId(shopId)
                .shopName(shop.getShopName())
                .totalProducts(productRepository.countByShopShopId(shopId))
                .activeProducts(productRepository.countByShopShopIdAndStatus(shopId, 1))
                .pendingApprovalProducts(productRepository.countByShopShopIdAndIsApproved(shopId, false))
                .pendingOrders(orderRepository.countByShopShopIdAndStatus(shopId, OrderStatus.PENDING))
                .shippingOrders(orderRepository.countByShopShopIdAndStatus(shopId, OrderStatus.SHIPPING))
                .completedOrders(orderRepository.countByShopShopIdAndStatusIn(shopId, REVENUE_STATUSES))
                .totalRevenue(orderRepository.sumTotalAmountByShopAndStatusIn(shopId, REVENUE_STATUSES))
                .todayRevenue(orderRepository.sumTotalAmountByShopAndStatusInAndCreatedAtBetween(
                        shopId, REVENUE_STATUSES, startOfDay, endOfDay))
                .build();
    }

    @Override
    public ShopProfileResponse getShopProfile() {
        ShopEntity shop = requireShop();
        long productCount = productRepository.countByShopShopId(shop.getShopId());
        return ShopProfileResponse.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .status(shop.getStatus())
                .productCount((int) productCount)
                .build();
    }

    @Override
    @Transactional
    public ShopProfileResponse updateShopProfile(UpdateShopRequest request) {
        ShopEntity shop = requireShop();
        if (request.getShopName() != null && !request.getShopName().isBlank()) {
            shop.setShopName(request.getShopName().trim());
        }
        if (request.getDescription() != null) {
            shop.setDescription(request.getDescription().trim());
        }
        shopRepository.save(shop);
        return getShopProfile();
    }

    @Override
    public Page<ProductResponse> getProducts(int page, int pageSize) {
        ShopEntity shop = requireShop();
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findByShopShopId(shop.getShopId(), pageable)
                .map(productMapper::toProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        ShopEntity shop = requireShop();
        validateProductRequest(request.getProductName(), request.getPrice());

        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh muc khong ton tai"));
        }

        boolean autoApproved = category != null
                && category.getModerationLevel() != null
                && category.getModerationLevel() == 1;

        ProductEntity product = ProductEntity.builder()
                .productName(request.getProductName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .imageUrl(request.getImageUrl())
                .attributes(request.getAttributes())
                .category(category)
                .shop(shop)
                .status(1)
                .averageRating(0F)
                .salesCount(0)
                .isApproved(autoApproved)
                .build();

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        ProductEntity product = requireOwnedProduct(productId);

        if (request.getProductName() != null && !request.getProductName().isBlank()) {
            product.setProductName(request.getProductName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getAttributes() != null) {
            product.setAttributes(request.getAttributes());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh muc khong ton tai"));
            product.setCategory(category);
            product.setIsApproved(category.getModerationLevel() != null && category.getModerationLevel() == 1);
        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProductVisibility(Long productId, VisibilityRequest request) {
        ProductEntity product = requireOwnedProduct(productId);
        if (request.getStatus() == null) {
            throw new RuntimeException("Trang thai khong hop le");
        }
        product.setStatus(request.getStatus());
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updatePriceQuantity(Long productId, UpdatePriceQuantityRequest request) {
        ProductEntity product = requireOwnedProduct(productId);
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        ProductEntity product = requireOwnedProduct(productId);
        productRepository.delete(product);
    }

    @Override
    public Page<SellerOrderResponse> getOrders(Integer status, int page, int pageSize) {
        ShopEntity shop = requireShop();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<OrderEntity> orders = status != null
                ? orderRepository.findByShopShopIdAndStatus(shop.getShopId(), status, pageable)
                : orderRepository.findByShopShopId(shop.getShopId(), pageable);
        return orders.map(this::toOrderResponse);
    }

    @Override
    public SellerOrderResponse getOrderDetail(Long orderId) {
        OrderEntity order = requireOwnedOrder(orderId);
        return toOrderResponse(order);
    }

    @Override
    @Transactional
    public SellerOrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        OrderEntity order = requireOwnedOrder(orderId);
        if (request.getStatus() == null) {
            throw new RuntimeException("Trang thai don hang khong hop le");
        }
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        orderRepository.save(order);

        String trackingStatus = mapTrackingStatus(request.getStatus());
        OrderTrackingEntity tracking = OrderTrackingEntity.builder()
                .order(order)
                .status(trackingStatus)
                .location(request.getLocation())
                .build();
        orderTrackingRepository.save(tracking);

        if (request.getStatus() == OrderStatus.CANCELLED) {
            restoreStock(orderId);
        }

        return toOrderResponse(order);
    }

    @Override
    public SellerRevenueResponse getRevenue(LocalDate from, LocalDate to) {
        ShopEntity shop = requireShop();
        LocalDate start = from != null ? from : LocalDate.now().minusDays(30);
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDateTime fromDt = start.atStartOfDay();
        LocalDateTime toDt = end.plusDays(1).atStartOfDay();

        List<OrderEntity> orders = orderRepository.findByShopAndStatusInAndCreatedAtBetween(
                shop.getShopId(), REVENUE_STATUSES, fromDt, toDt);

        Map<LocalDate, List<OrderEntity>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate()));

        List<SellerRevenueResponse.RevenueByDay> byDay = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> SellerRevenueResponse.RevenueByDay.builder()
                        .date(entry.getKey().toString())
                        .revenue(entry.getValue().stream().mapToLong(OrderEntity::getTotalAmount).sum())
                        .orderCount(entry.getValue().size())
                        .build())
                .collect(Collectors.toList());

        long totalRevenue = orders.stream().mapToLong(OrderEntity::getTotalAmount).sum();

        return SellerRevenueResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(orderRepository.countByShopShopId(shop.getShopId()))
                .completedOrders(orders.size())
                .byDay(byDay)
                .build();
    }

    @Override
    public Page<SellerReviewResponse> getReviews(int page, int pageSize) {
        ShopEntity shop = requireShop();
        Pageable pageable = PageRequest.of(page, pageSize);
        return reviewRepository.findByShopId(shop.getShopId(), pageable)
                .map(this::toReviewResponse);
    }

    @Override
    @Transactional
    public SellerReviewResponse replyToReview(Long reviewId, CreateReplyRequest request) {
        ShopEntity shop = requireShop();
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new RuntimeException("Noi dung phan hoi khong duoc de trong");
        }

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Danh gia khong ton tai"));

        if (review.getProduct() == null
                || review.getProduct().getShop() == null
                || !review.getProduct().getShop().getShopId().equals(shop.getShopId())) {
            throw new RuntimeException("Ban khong co quyen tra loi danh gia nay");
        }

        ReplyEntity existing = replyRepository.findFirstByReviewReviewId(reviewId).orElse(null);

        ReplyEntity reply;
        if (existing != null) {
            existing.setContent(request.getContent().trim());
            reply = replyRepository.save(existing);
        } else {
            reply = ReplyEntity.builder()
                    .review(review)
                    .shop(shop)
                    .content(request.getContent().trim())
                    .build();
            reply = replyRepository.save(reply);
        }

        SellerReviewResponse response = toReviewResponse(review);
        response.setReplyId(reply.getId());
        response.setReplyContent(reply.getContent());
        response.setReplyCreatedAt(reply.getCreatedAt());
        return response;
    }

    @Override
    public List<VoucherResponse> getVouchers() {
        ShopEntity shop = requireShop();
        return voucherRepository.findByShopShopIdOrderByCreatedAtDesc(shop.getShopId()).stream()
                .map(this::toVoucherResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        ShopEntity shop = requireShop();
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new RuntimeException("Ma voucher khong duoc de trong");
        }
        if (voucherRepository.existsByCode(request.getCode().trim().toUpperCase())) {
            throw new RuntimeException("Ma voucher da ton tai");
        }

        VoucherEntity voucher = VoucherEntity.builder()
                .code(request.getCode().trim().toUpperCase())
                .discountType(request.getDiscountType() != null ? request.getDiscountType() : "PERCENT")
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 100)
                .usedCount(0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(1)
                .shop(shop)
                .build();

        return toVoucherResponse(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public VoucherResponse updateVoucher(Long voucherId, UpdateVoucherRequest request) {
        ShopEntity shop = requireShop();
        VoucherEntity voucher = voucherRepository.findByVoucherIdAndShopShopId(voucherId, shop.getShopId())
                .orElseThrow(() -> new RuntimeException("Voucher khong ton tai"));

        if (request.getDiscountType() != null) {
            voucher.setDiscountType(request.getDiscountType());
        }
        if (request.getDiscountValue() != null) {
            voucher.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMinOrderValue() != null) {
            voucher.setMinOrderValue(request.getMinOrderValue());
        }
        if (request.getMaxDiscount() != null) {
            voucher.setMaxDiscount(request.getMaxDiscount());
        }
        if (request.getUsageLimit() != null) {
            voucher.setUsageLimit(request.getUsageLimit());
        }
        if (request.getStartDate() != null) {
            voucher.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            voucher.setEndDate(request.getEndDate());
        }
        if (request.getIsActive() != null) {
            voucher.setIsActive(request.getIsActive());
        }

        return toVoucherResponse(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public void deleteVoucher(Long voucherId) {
        ShopEntity shop = requireShop();
        VoucherEntity voucher = voucherRepository.findByVoucherIdAndShopShopId(voucherId, shop.getShopId())
                .orElseThrow(() -> new RuntimeException("Voucher khong ton tai"));
        voucherRepository.delete(voucher);
    }

    private ProductEntity requireOwnedProduct(Long productId) {
        ShopEntity shop = requireShop();
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("San pham khong ton tai"));
        if (product.getShop() == null || !product.getShop().getShopId().equals(shop.getShopId())) {
            throw new RuntimeException("Ban khong co quyen thao tac san pham nay");
        }
        return product;
    }

    private OrderEntity requireOwnedOrder(Long orderId) {
        ShopEntity shop = requireShop();
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Don hang khong ton tai"));
        if (order.getShop() == null || !order.getShop().getShopId().equals(shop.getShopId())) {
            throw new RuntimeException("Ban khong co quyen thao tac don hang nay");
        }
        return order;
    }

    private void validateProductRequest(String name, Long price) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Ten san pham khong duoc de trong");
        }
        if (price == null || price <= 0) {
            throw new RuntimeException("Gia san pham phai lon hon 0");
        }
    }

    private void validateStatusTransition(int current, int next) {
        boolean valid = switch (current) {
            case OrderStatus.PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case OrderStatus.CONFIRMED -> next == OrderStatus.SHIPPING || next == OrderStatus.CANCELLED;
            case OrderStatus.SHIPPING -> next == OrderStatus.DELIVERED || next == OrderStatus.CANCELLED;
            case OrderStatus.DELIVERED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new RuntimeException(
                    "Khong the chuyen tu " + OrderStatus.label(current) + " sang " + OrderStatus.label(next));
        }
    }

    private String mapTrackingStatus(int status) {
        return switch (status) {
            case OrderStatus.CONFIRMED -> "CONFIRMED";
            case OrderStatus.SHIPPING -> "SHIPPING";
            case OrderStatus.DELIVERED -> "DELIVERED";
            case OrderStatus.CANCELLED -> "CANCELLED";
            case OrderStatus.COMPLETED -> "COMPLETED";
            default -> "UPDATED";
        };
    }

    private void restoreStock(Long orderId) {
        List<OrderDetailEntity> details = orderDetailRepository.findByOrderOrderId(orderId);
        for (OrderDetailEntity detail : details) {
            ProductEntity product = detail.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + detail.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private SellerOrderResponse toOrderResponse(OrderEntity order) {
        List<OrderDetailEntity> details = orderDetailRepository.findByOrderOrderId(order.getOrderId());
        List<SellerOrderItemResponse> items = details.stream()
                .map(d -> SellerOrderItemResponse.builder()
                        .productId(d.getProduct() != null ? d.getProduct().getProductId() : null)
                        .productName(d.getProduct() != null ? d.getProduct().getProductName() : null)
                        .imageUrl(d.getProduct() != null ? d.getProduct().getImageUrl() : null)
                        .quantity(d.getQuantity())
                        .priceAtPurchase(d.getPriceAtPurchase() != null ? d.getPriceAtPurchase().longValue() : 0L)
                        .variantLabel(d.getVariantLabel())
                        .lineTotal((d.getPriceAtPurchase() != null ? d.getPriceAtPurchase().longValue() : 0L)
                                * d.getQuantity())
                        .build())
                .toList();

        long subtotal = items.stream().mapToLong(item -> item.getLineTotal() != null ? item.getLineTotal() : 0L).sum();
        long shippingFee = 30000L;
        long discountAmount = Math.max(0L,
                subtotal + shippingFee - (order.getTotalAmount() != null ? order.getTotalAmount() : 0L));

        return SellerOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus())
                .statusLabel(OrderStatus.label(order.getStatus()))
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress())
                .buyerName(order.getUser() != null ? order.getUser().getFullName() : null)
                .createdAt(order.getCreatedAt())
                .items(items)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .voucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null)
                .build();
    }

    private SellerReviewResponse toReviewResponse(ReviewEntity review) {
        SellerReviewResponse.SellerReviewResponseBuilder builder = SellerReviewResponse.builder()
                .reviewId(review.getReviewId())
                .productId(review.getProduct() != null ? review.getProduct().getProductId() : null)
                .productName(review.getProduct() != null ? review.getProduct().getProductName() : null)
                .buyerName(review.getUser() != null ? review.getUser().getFullName() : null)
                .star(review.getStar())
                .content(review.getContent())
                .createdAt(review.getCreatedAt());

        replyRepository.findFirstByReviewReviewId(review.getReviewId())
                .ifPresent(reply -> builder.replyId(reply.getId())
                        .replyContent(reply.getContent())
                        .replyCreatedAt(reply.getCreatedAt()));

        return builder.build();
    }

    private VoucherResponse toVoucherResponse(VoucherEntity voucher) {
        return VoucherResponse.builder()
                .voucherId(voucher.getVoucherId())
                .code(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .maxDiscount(voucher.getMaxDiscount())
                .usageLimit(voucher.getUsageLimit())
                .usedCount(voucher.getUsedCount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .isActive(voucher.getIsActive())
                .build();
    }

    @Override
    public SellerStatisticsResponse getAdvancedStatistics(LocalDate from, LocalDate to) {
        ShopEntity shop = requireShop();
        Long shopId = shop.getShopId();

        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(30);
        LocalDateTime currentStart = start.atStartOfDay();
        LocalDateTime currentEnd = end.plusDays(1).atStartOfDay();

        long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
        LocalDateTime previousStart = start.minusDays(daysBetween).atStartOfDay();
        LocalDateTime previousEnd = currentStart;

        Pageable top5 = PageRequest.of(0, 5);

        List<Object[]> topSellingRaw = orderDetailRepository.findTopSellingProducts(
                shopId, REVENUE_STATUSES, currentStart, currentEnd, top5);
        List<SellerStatisticsResponse.ProductStatistic> topSelling = mapToProductStatistic(topSellingRaw);

        List<Object[]> topRevenueRaw = orderDetailRepository.findTopRevenueProducts(
                shopId, REVENUE_STATUSES, currentStart, currentEnd, top5);
        List<SellerStatisticsResponse.ProductStatistic> topRevenue = mapToProductStatistic(topRevenueRaw);

        List<OrderEntity> currentOrders = orderRepository.findByShopAndStatusInAndCreatedAtBetween(
                shopId, REVENUE_STATUSES, currentStart, currentEnd);

        long grossRevenue = 0;
        long totalVoucherDiscount = 0;
        long totalPlatformFee = 0;

        for (OrderEntity order : currentOrders) {
            long orderTotal = order.getTotalAmount() != null ? order.getTotalAmount() : 0;

            // Voucher discount = tong tien voucher duoc giam trong don hang nay
            long voucherDiscountAmount = 0;
            if (order.getVoucher() != null) {
                VoucherEntity v = order.getVoucher();
                if ("PERCENT".equals(v.getDiscountType())) {
                    double discounted = orderTotal * (v.getDiscountValue() / 100.0);
                    if (v.getMaxDiscount() != null) discounted = Math.min(discounted, v.getMaxDiscount());
                    voucherDiscountAmount = (long) discounted;
                } else {
                    voucherDiscountAmount = v.getDiscountValue() != null ? v.getDiscountValue().longValue() : 0;
                }
            }

            long revenueAfterVoucher = orderTotal + voucherDiscountAmount; // khoi phuc lai truoc khi giam
            grossRevenue += revenueAfterVoucher;
            totalVoucherDiscount += voucherDiscountAmount;

            long fee = (long) (orderTotal * 0.03); // 3% platform fee tren so tien thuc tra
            totalPlatformFee += fee;
        }

        long netRevenue = grossRevenue - totalVoucherDiscount - totalPlatformFee;

        SellerStatisticsResponse.ActualRevenueInfo actualRevenue = SellerStatisticsResponse.ActualRevenueInfo.builder()
                .grossRevenue(grossRevenue)
                .platformFee(totalPlatformFee)
                .voucherDiscount(totalVoucherDiscount)
                .netRevenue(netRevenue)
                .build();

        long currentPeriodRevenue = currentOrders.stream()
                .mapToLong(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0)
                .sum();

        List<OrderEntity> previousOrders = orderRepository.findByShopAndStatusInAndCreatedAtBetween(
                shopId, REVENUE_STATUSES, previousStart, previousEnd);

        long previousPeriodRevenue = previousOrders.stream()
                .mapToLong(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0)
                .sum();

        double growthRate = 0;
        if (previousPeriodRevenue > 0) {
            growthRate = ((double) (currentPeriodRevenue - previousPeriodRevenue) / previousPeriodRevenue) * 100.0;
        } else if (currentPeriodRevenue > 0) {
            growthRate = 100.0;
        }

        SellerStatisticsResponse.RevenueComparison revenueComparison = SellerStatisticsResponse.RevenueComparison
                .builder()
                .currentPeriodRevenue(currentPeriodRevenue)
                .previousPeriodRevenue(previousPeriodRevenue)
                .growthRate(Math.round(growthRate * 100.0) / 100.0)
                .build();

        double averageOrderValue = currentOrders.isEmpty() ? 0 : (double) currentPeriodRevenue / currentOrders.size();

        SellerStatisticsResponse.AverageMetrics averageMetrics = SellerStatisticsResponse.AverageMetrics.builder()
                .averageOrderValue(Math.round(averageOrderValue * 100.0) / 100.0)
                .build();

        return SellerStatisticsResponse.builder()
                .actualRevenue(actualRevenue)
                .topSellingProducts(topSelling)
                .topRevenueProducts(topRevenue)
                .revenueComparison(revenueComparison)
                .averageMetrics(averageMetrics)
                .build();
    }

    private List<SellerStatisticsResponse.ProductStatistic> mapToProductStatistic(List<Object[]> rawList) {
        List<SellerStatisticsResponse.ProductStatistic> result = new ArrayList<>();
        for (Object[] row : rawList) {
            result.add(SellerStatisticsResponse.ProductStatistic.builder()
                    .productId(row[0] != null ? ((Number) row[0]).longValue() : null)
                    .productName((String) row[1])
                    .variantLabel((String) row[2])
                    .imageUrl((String) row[3])
                    .totalQuantitySold(row[4] != null ? ((Number) row[4]).longValue() : 0)
                    .totalRevenue(row[5] != null ? ((Number) row[5]).longValue() : 0)
                    .build());
        }
        return result;
    }
}
