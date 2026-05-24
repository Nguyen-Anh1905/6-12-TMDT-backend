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

    List<ReviewEntity> findByProductProductIdOrderByCreatedAtDesc(Long productId);

    boolean existsByUserUserIdAndProductProductIdAndOrderOrderId(Long userId, Long productId, Long orderId);
}
