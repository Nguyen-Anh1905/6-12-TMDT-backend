package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.CreateVoucherRequest;
import com.example.backend_tmdt.dto.request.UpdateVoucherRequest;
import com.example.backend_tmdt.dto.response.VoucherListResponse;
import com.example.backend_tmdt.dto.response.VoucherResponse;
import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.entity.VoucherEntity;
import com.example.backend_tmdt.repository.ShopRepository;
import com.example.backend_tmdt.repository.VoucherRepository;
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
public class AdminVoucherService {

    private final VoucherRepository voucherRepository;
    private final ShopRepository shopRepository;

    /**
     * Lấy danh sách voucher sàn (admin) với pagination
     * @param page Trang (0-based)
     * @param size Số vouchers trên 1 trang
     * @return VoucherListResponse
     */
    public VoucherListResponse getAllVouchers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VoucherEntity> vouchers = voucherRepository.findAll(pageable);

        List<VoucherResponse> content = vouchers.getContent().stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());

        return VoucherListResponse.builder()
                .content(content)
                .currentPage(page)
                .pageSize(size)
                .totalElements(vouchers.getTotalElements())
                .totalPages(vouchers.getTotalPages())
                .build();
    }

    /**
     * Lấy chi tiết 1 voucher
     * @param voucherId ID của voucher
     * @return VoucherResponse
     */
    public VoucherResponse getVoucherById(Long voucherId) {
        VoucherEntity voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher!"));
        return entityToResponse(voucher);
    }

    /**
     * Tạo voucher sàn
     * @param request Thông tin voucher
     */
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        // Kiểm tra mã voucher đã tồn tại
        voucherRepository.findAll().stream()
                .filter(v -> v.getCode().equals(request.getCode()))
                .findAny()
                .ifPresent(v -> {
                    throw new RuntimeException("Mã voucher '" + request.getCode() + "' đã tồn tại!");
                });

        // Kiểm tra shop tồn tại
        ShopEntity shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Cửa hàng không tồn tại!"));

        // Kiểm tra ngày bắt đầu < ngày kết thúc
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc!");
        }

        VoucherEntity voucher = VoucherEntity.builder()
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : 0)
                .maxDiscount(request.getMaxDiscount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 0)
                .usedCount(0)
                .isActive(1)  // Mặc định active
                .shop(shop)
                .build();

        VoucherEntity saved = voucherRepository.save(voucher);
        return entityToResponse(saved);
    }

    /**
     * Cập nhật voucher
     * @param voucherId ID của voucher
     * @param request Thông tin cập nhật
     */
    @Transactional
    public VoucherResponse updateVoucher(Long voucherId, UpdateVoucherRequest request) {
        VoucherEntity voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher!"));

        // Kiểm tra ngày bắt đầu < ngày kết thúc
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc!");
        }

        voucher.setCode(request.getCode());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : 0);
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setUsageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 0);
        voucher.setIsActive(request.getIsActive());

        VoucherEntity updated = voucherRepository.save(voucher);
        return entityToResponse(updated);
    }

    /**
     * Xóa voucher
     * @param voucherId ID của voucher
     */
    @Transactional
    public void deleteVoucher(Long voucherId) {
        VoucherEntity voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher!"));

        voucherRepository.delete(voucher);
    }

    /**
     * Helper: Chuyển VoucherEntity thành VoucherResponse
     */
    private VoucherResponse entityToResponse(VoucherEntity voucher) {
        return VoucherResponse.builder()
                .voucherId(voucher.getVoucherId())
                .code(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .maxDiscount(voucher.getMaxDiscount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .usageLimit(voucher.getUsageLimit())
                .usedCount(voucher.getUsedCount())
                .isActive(voucher.getIsActive())
                .shopId(voucher.getShop().getShopId())
                .shopName(voucher.getShop().getShopName())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .build();
    }
}
