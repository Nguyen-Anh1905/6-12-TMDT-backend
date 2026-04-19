package com.example.backend_tmdt.mapper;

import com.example.backend_tmdt.dto.response.ProductResponse;
import com.example.backend_tmdt.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return ProductResponse.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .imageUrl(entity.getImageUrl())
                .attributes(entity.getAttributes())
                .status(entity.getStatus())
                .averageRating(entity.getAverageRating())
                .salesCount(entity.getSalesCount())
                .isApproved(entity.getIsApproved())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getCategoryId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getCategoryName() : null)
                .shopId(entity.getShop() != null ? entity.getShop().getShopId() : null)
                .shopName(entity.getShop() != null ? entity.getShop().getShopName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
