package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.ProductApprovalRequest;
import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.ApprovalProductResponse;
import com.example.backend_tmdt.dto.response.ProductApprovalListResponse;
import com.example.backend_tmdt.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * Danh sách sản phẩm chờ duyệt (có pagination)
     * GET /api/admin/products/approval?page=0&size=10
     */
    @GetMapping("/approval")
    public ResponseEntity<ApiResponse<ProductApprovalListResponse>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ProductApprovalListResponse response = adminProductService.getPendingProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách sản phẩm chờ duyệt thành công!"));
    }

    /**
     * Chi tiết một sản phẩm
     * GET /api/admin/products/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ApprovalProductResponse>> getProductById(@PathVariable Long productId) {
        ApprovalProductResponse response = adminProductService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin sản phẩm thành công!"));
    }

    /**
     * Duyệt sản phẩm (phê duyệt)
     * POST /api/admin/products/{productId}/approve
     * Body: { "decision": "approve" }
     */
    @PostMapping("/{productId}/approve")
    public ResponseEntity<ApiResponse<ApprovalProductResponse>> approveProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductApprovalRequest request) {
        ApprovalProductResponse response = adminProductService.approveProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Duyệt sản phẩm thành công!"));
    }

    /**
     * Từ chối sản phẩm (rejection)
     * POST /api/admin/products/{productId}/reject
     * Body: { "decision": "reject", "reason": "Nội dung không phù hợp..." }
     */
    @PostMapping("/{productId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductApprovalRequest request) {
        adminProductService.rejectProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Từ chối sản phẩm thành công!"));
    }
}
