package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.ProductResponse;
import com.example.backend_tmdt.entity.ProductEntity;
import com.example.backend_tmdt.entity.UserEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByLoginKey(username).orElse(null);
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getSellerProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        UserEntity user = getCurrentUser();
        if (user == null || user.getShop() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(9999, "Dang nhap required"));
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductEntity> products = productRepository.findByShopShopId(user.getShop().getShopId(), pageable);
        Page<ProductResponse> response = products.map(this::mapToResponse);
        return ResponseEntity.ok(ApiResponse.success(response, "Lay danh sach san pham thanh cong"));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductEntity product) {
        try {
            UserEntity user = getCurrentUser();
            if (user == null || user.getShop() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(9999, "Dang nhap required"));
            }

            product.setShop(user.getShop());

            if (product.getAverageRating() == null) {
                product.setAverageRating(0F);
            }
            if (product.getSalesCount() == null) {
                product.setSalesCount(0);
            }
            if (product.getIsApproved() == null) {
                product.setIsApproved(false);
            }

            ProductEntity saved = productRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(mapToResponse(saved), "Tao san pham thanh cong"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(9999, "Tao san pham that bai: " + e.getMessage()));
        }
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductEntity productUpdate
    ) {
        try {
            UserEntity user = getCurrentUser();
            if (user == null || user.getShop() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(9999, "Dang nhap required"));
            }

            ProductEntity existing = productRepository.findById(productId).orElse(null);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(9999, "San pham khong ton tai"));
            }

            if (existing.getShop() == null || !existing.getShop().getShopId().equals(user.getShop().getShopId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(9999, "Ban khong co quyen sua san pham nay"));
            }

            existing.setProductName(productUpdate.getProductName());
            existing.setDescription(productUpdate.getDescription());
            existing.setPrice(productUpdate.getPrice());
            existing.setStockQuantity(productUpdate.getStockQuantity());
            existing.setImageUrl(productUpdate.getImageUrl());
            existing.setCategory(productUpdate.getCategory());
            existing.setAttributes(productUpdate.getAttributes());

            ProductEntity updated = productRepository.save(existing);
            return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated), "Cap nhat san pham thanh cong"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(9999, "Cap nhat san pham that bai: " + e.getMessage()));
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        try {
            UserEntity user = getCurrentUser();
            if (user == null || user.getShop() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(9999, "Dang nhap required"));
            }

            ProductEntity product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(9999, "San pham khong ton tai"));
            }

            if (product.getShop() == null || !product.getShop().getShopId().equals(user.getShop().getShopId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(9999, "Ban khong co quyen xoa san pham nay"));
            }

            productRepository.deleteById(productId);
            return ResponseEntity.ok(ApiResponse.success("Xoa san pham thanh cong"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(9999, "Xoa san pham that bai: " + e.getMessage()));
        }
    }

    private ProductResponse mapToResponse(ProductEntity product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setAttributes(product.getAttributes());
        response.setAverageRating(product.getAverageRating());
        response.setSalesCount(product.getSalesCount());
        response.setIsApproved(product.getIsApproved());

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getCategoryId());
            response.setCategoryName(product.getCategory().getCategoryName());
        }

        if (product.getShop() != null) {
            response.setShopName(product.getShop().getShopName());
        }

        return response;
    }
}
