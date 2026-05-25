package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("SELECT r FROM ReviewEntity r WHERE r.product.shop.shopId = :shopId")
    Page<ReviewEntity> findByShopId(@Param("shopId") Long shopId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.product.shop.shopId = :shopId")
    long countByShopId(@Param("shopId") Long shopId);

    @Query("SELECT COALESCE(AVG(r.star), 0) FROM ReviewEntity r WHERE r.product.shop.shopId = :shopId")
    Double averageStarByShopId(@Param("shopId") Long shopId);

    List<ReviewEntity> findByProductProductIdOrderByCreatedAtDesc(Long productId);

    List<ReviewEntity> findByOrderOrderIdAndUserUserId(Long orderId, Long userId);

    boolean existsByUserUserIdAndProductProductIdAndOrderOrderId(Long userId, Long productId, Long orderId);
}
