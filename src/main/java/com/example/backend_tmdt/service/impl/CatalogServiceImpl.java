package com.example.backend_tmdt.service.impl;

import com.example.backend_tmdt.dto.response.ProductReviewResponse;
import com.example.backend_tmdt.entity.ReplyEntity;
import com.example.backend_tmdt.entity.ReviewEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.ReplyRepository;
import com.example.backend_tmdt.repository.ReviewRepository;
import com.example.backend_tmdt.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;

    @Override
    public List<ProductReviewResponse> getProductReviews(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("San pham khong ton tai"));

        return reviewRepository.findByProductProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    private ProductReviewResponse toReviewResponse(ReviewEntity review) {
        ProductReviewResponse.ProductReviewResponseBuilder builder = ProductReviewResponse.builder()
                .reviewId(review.getReviewId())
                .buyerName(review.getUser() != null ? review.getUser().getFullName() : "Khach hang")
                .star(review.getStar())
                .content(review.getContent())
                .createdAt(review.getCreatedAt());

        replyRepository.findFirstByReviewReviewId(review.getReviewId())
                .map(ReplyEntity::getContent)
                .ifPresent(builder::shopReply);

        return builder.build();
    }
}
