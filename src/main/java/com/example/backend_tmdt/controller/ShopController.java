package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.ShopProfileResponse;
import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.ReviewRepository;
import com.example.backend_tmdt.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
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
    private final ReviewRepository reviewRepository;

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopProfileResponse>> getShopProfile(@PathVariable long shopId) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop khong ton tai"));

        if (shop.getStatus() == null || shop.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop khong hoat dong");
        }

        long productCount = productRepository.countByShopShopIdAndStatus(shopId, 1);
        long reviewCount = reviewRepository.countByShopId(shopId);
        Double averageRating = reviewRepository.averageStarByShopId(shopId);

        ShopProfileResponse response = ShopProfileResponse.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .status(shop.getStatus())
            .averageRating(averageRating != null ? averageRating.floatValue() : 0F)
            .reviewCount(reviewCount)
                .productCount((int) productCount)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Lay thong tin shop thanh cong"));
    }
}
