package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.CreateProductRequest;
import com.example.backend_tmdt.dto.request.SearchProductRequest;
import com.example.backend_tmdt.dto.request.UpdateProductRequest;
import com.example.backend_tmdt.dto.response.ProductListResponse;
import com.example.backend_tmdt.dto.response.ProductResponse;
import com.example.backend_tmdt.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    ProductListResponse getVisibleProducts(Integer page, Integer pageSize);

    ProductListResponse searchProducts(SearchProductRequest request);

    ProductResponse getProductDetails(Long productId);

    ProductListResponse getProductsByShop(Long shopId, Integer page, Integer pageSize);

    ProductListResponse getProductsByCategory(Long categoryId, Integer page, Integer pageSize);

    void deleteProduct(Long productId);

}
