package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.CreateVoucherRequest;
import com.example.backend_tmdt.dto.request.UpdateVoucherRequest;
import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.VoucherListResponse;
import com.example.backend_tmdt.dto.response.VoucherResponse;
import com.example.backend_tmdt.service.AdminVoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/vouchers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminVoucherController {

    private final AdminVoucherService adminVoucherService;

    /**
     * Danh sách voucher sàn (có pagination)
     * GET /api/admin/vouchers?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<VoucherListResponse>> getAllVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        VoucherListResponse response = adminVoucherService.getAllVouchers(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách voucher thành công!"));
    }

    /**
     * Chi tiết một voucher
     * GET /api/admin/vouchers/{voucherId}
     */
    @GetMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucherById(@PathVariable Long voucherId) {
        VoucherResponse response = adminVoucherService.getVoucherById(voucherId);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin voucher thành công!"));
    }

    /**
     * Tạo voucher sàn
     * POST /api/admin/vouchers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(@Valid @RequestBody CreateVoucherRequest request) {
        VoucherResponse response = adminVoucherService.createVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo voucher thành công!"));
    }

    /**
     * Cập nhật voucher
     * PUT /api/admin/vouchers/{voucherId}
     */
    @PutMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> updateVoucher(
            @PathVariable Long voucherId,
            @Valid @RequestBody UpdateVoucherRequest request) {
        VoucherResponse response = adminVoucherService.updateVoucher(voucherId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật voucher thành công!"));
    }

    /**
     * Xóa voucher
     * DELETE /api/admin/vouchers/{voucherId}
     */
    @DeleteMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable Long voucherId) {
        adminVoucherService.deleteVoucher(voucherId);
        return ResponseEntity.ok(ApiResponse.success("Xóa voucher thành công!"));
    }
}
