package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.ProductApprovalRequest;
import com.example.backend_tmdt.dto.response.ApprovalProductResponse;
import com.example.backend_tmdt.dto.response.ProductApprovalListResponse;
import com.example.backend_tmdt.entity.ProductEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;

    /**
     * Lấy danh sách sản phẩm chờ duyệt (với pagination)
     * @param page Trang (0-based)
     * @param size Số sản phẩm trên 1 trang
     * @return ProductApprovalListResponse
     */
    public ProductApprovalListResponse getPendingProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Lấy các sản phẩm chưa được duyệt (isApproved = false)
        Page<ProductEntity> products = productRepository.findByIsApproved(false, pageable);

        List<ApprovalProductResponse> content = products.getContent().stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());

        return ProductApprovalListResponse.builder()
                .content(content)
                .currentPage(page)
                .pageSize(size)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .build();
    }

    /**
     * Lấy chi tiết 1 sản phẩm
     * @param productId ID của sản phẩm
     * @return ApprovalProductResponse
     */
    public ApprovalProductResponse getProductById(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
        return entityToResponse(product);
    }

    /**
     * Duyệt sản phẩm (phê duyệt)
     * @param productId ID của sản phẩm
     * @param request Yêu cầu (decision: approve/reject)
     */
    @Transactional
    public ApprovalProductResponse approveProduct(Long productId, ProductApprovalRequest request) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        if (!request.getDecision().equalsIgnoreCase("approve")) {
            throw new RuntimeException("Decision phải là 'approve'!");
        }

        product.setIsApproved(true);
        ProductEntity updated = productRepository.save(product);
        return entityToResponse(updated);
    }

    /**
     * Từ chối sản phẩm (rejection)
     * @param productId ID của sản phẩm
     * @param request Yêu cầu (decision: reject, reason: lý do)
     */
    @Transactional
    public void rejectProduct(Long productId, ProductApprovalRequest request) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        if (!request.getDecision().equalsIgnoreCase("reject")) {
            throw new RuntimeException("Decision phải là 'reject'!");
        }

        // Set status = 0 (ẩn sản phẩm) và giữ isApproved = false
        product.setStatus(0);
        // Optional: có thể lưu reason vào description hoặc tạo entity riêng để track lý do từ chối
        productRepository.save(product);
    }

    /**
     * Helper: Chuyển ProductEntity thành ApprovalProductResponse
     */
    private ApprovalProductResponse entityToResponse(ProductEntity product) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getCategoryId() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getCategoryName() : null;

        return ApprovalProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .isApproved(product.getIsApproved())
                .shopId(product.getShop().getShopId())
                .shopName(product.getShop().getShopName())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
