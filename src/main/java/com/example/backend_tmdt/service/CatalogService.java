package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.response.ProductReviewResponse;

import java.util.List;

public interface CatalogService {

    List<ProductReviewResponse> getProductReviews(Long productId);
}
