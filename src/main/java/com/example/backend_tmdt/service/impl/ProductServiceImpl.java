package com.example.backend_tmdt.service.impl;

import com.example.backend_tmdt.dto.request.CreateProductRequest;
import com.example.backend_tmdt.dto.request.SearchProductRequest;
import com.example.backend_tmdt.dto.request.UpdateProductRequest;
import com.example.backend_tmdt.dto.response.ProductListResponse;
import com.example.backend_tmdt.dto.response.ProductResponse;
import com.example.backend_tmdt.entity.CategoryEntity;
import com.example.backend_tmdt.entity.ProductEntity;
import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.mapper.ProductMapper;
import com.example.backend_tmdt.repository.CategoryRepository;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.ShopRepository;
import com.example.backend_tmdt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        boolean autoApproved = isAutoApprovedCategory(category);

        ShopEntity shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        ProductEntity product = ProductEntity.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .attributes(request.getAttributes())
                .category(category)
                .shop(shop)
                .status(1)
                .averageRating(0F)
                .salesCount(0)
                .isApproved(autoApproved)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductEntity saved = productRepository.save(product);
        return productMapper.toProductResponse(saved);
    }

    private boolean isAutoApprovedCategory(CategoryEntity category) {
        return category != null && category.getModerationLevel() != null && category.getModerationLevel() == 1;
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
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
            CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        product.setUpdatedAt(LocalDateTime.now());
        ProductEntity updated = productRepository.save(product);
        return productMapper.toProductResponse(updated);
    }


    @Override
    public ProductListResponse getVisibleProducts(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductEntity> productsPage = productRepository.findByStatusAndIsApproved(1, true, pageable);

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(page)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public ProductListResponse searchProducts(SearchProductRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());

        Page<ProductEntity> productsPage;
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            if (request.getCategoryId() != null) {
                productsPage = productRepository.searchProductsByCategoryAndFilters(
                        request.getKeyword(),
                        request.getMinPrice() != null ? request.getMinPrice() : 0L,
                        request.getMaxPrice() != null ? request.getMaxPrice() : Long.MAX_VALUE,
                        request.getCategoryId(),
                        1,
                        true,
                        pageable
                );
            } else {
                productsPage = productRepository.searchProductsByFilters(
                        request.getKeyword(),
                        request.getMinPrice() != null ? request.getMinPrice() : 0L,
                        request.getMaxPrice() != null ? request.getMaxPrice() : Long.MAX_VALUE,
                        1,
                        true,
                        pageable
                );
            }
        } else if (request.getCategoryId() != null) {
                productsPage = productRepository.findByCategoryCategoryIdAndStatusAndIsApproved(
                    request.getCategoryId(), 1, true, pageable
            );
        } else {
            productsPage = productRepository.findByStatusAndIsApproved(1, true, pageable);
        }

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .build();
    }

    @Override
    public ProductResponse getProductDetails(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStatus() == 0 || !product.getIsApproved()) {
            throw new RuntimeException("This product is not available");
        }

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductListResponse getProductsByShop(Long shopId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductEntity> productsPage = productRepository.findByShopShopIdAndStatusAndIsApproved(
                shopId, 1, true, pageable
        );

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(page)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public void deleteProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.deleteById(productId);
    }
}
