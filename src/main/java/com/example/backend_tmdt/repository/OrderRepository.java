package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByShopShopId(Long shopId, Pageable pageable);

    Page<OrderEntity> findByShopShopIdAndStatus(Long shopId, Integer status, Pageable pageable);

    Page<OrderEntity> findByUserUserId(Long userId, Pageable pageable);

    Page<OrderEntity> findByUserUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    Optional<OrderEntity> findByOrderIdAndUserUserId(Long orderId, Long userId);

    long countByShopShopId(Long shopId);

    long countByShopShopIdAndStatus(Long shopId, Integer status);

    long countByShopShopIdAndStatusIn(Long shopId, Collection<Integer> statuses);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.shop.shopId = :shopId AND o.status IN :statuses")
    long sumTotalAmountByShopAndStatusIn(@Param("shopId") Long shopId, @Param("statuses") Collection<Integer> statuses);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.shop.shopId = :shopId AND o.status IN :statuses AND o.createdAt >= :from AND o.createdAt < :to")
    long sumTotalAmountByShopAndStatusInAndCreatedAtBetween(
            @Param("shopId") Long shopId,
            @Param("statuses") Collection<Integer> statuses,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("SELECT o FROM OrderEntity o WHERE o.shop.shopId = :shopId AND o.status IN :statuses AND o.createdAt >= :from AND o.createdAt < :to")
    List<OrderEntity> findByShopAndStatusInAndCreatedAtBetween(
            @Param("shopId") Long shopId,
            @Param("statuses") Collection<Integer> statuses,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
