package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.ShopProfileResponse;
import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopProfileResponse>> getShopProfile(@PathVariable Long shopId) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop khong ton tai"));

        if (shop.getStatus() == null || shop.getStatus() != 1) {
            throw new RuntimeException("Shop khong hoat dong");
        }

        long productCount = productRepository.countByShopShopIdAndStatus(shopId, 1);

        ShopProfileResponse response = ShopProfileResponse.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .status(shop.getStatus())
                .productCount((int) productCount)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Lay thong tin shop thanh cong"));
    }
}
