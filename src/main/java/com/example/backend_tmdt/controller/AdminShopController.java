package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.ShopProfileResponse;
import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shops")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminShopController {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopProfileResponse>>> getAllShops() {
        List<ShopProfileResponse> shops = shopRepository.findAll().stream()
                .map(shop -> ShopProfileResponse.builder()
                        .shopId(shop.getShopId())
                        .shopName(shop.getShopName())
                        .description(shop.getDescription())
                        .status(shop.getStatus())
                        .productCount((int) productRepository.countByShopShopId(shop.getShopId()))
                        .build())
                .toList();

        return ResponseEntity.ok(ApiResponse.success(shops, "Lay danh sach shop thanh cong"));
    }
}
