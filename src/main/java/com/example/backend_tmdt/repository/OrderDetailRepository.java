package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.OrderDetailEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    List<OrderDetailEntity> findByOrderOrderId(Long orderId);

    @Query("SELECT p.productId, p.productName, od.variantLabel, p.imageUrl, SUM(od.quantity), SUM(od.quantity * od.priceAtPurchase) " +
           "FROM OrderDetailEntity od " +
           "JOIN od.product p " +
           "JOIN od.order o " +
           "WHERE o.shop.shopId = :shopId " +
           "  AND o.status IN :statuses " +
           "  AND o.createdAt >= :startDate " +
           "  AND o.createdAt < :endDate " +
           "GROUP BY p.productId, p.productName, od.variantLabel, p.imageUrl " +
           "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findTopSellingProducts(
            @Param("shopId") Long shopId, 
            @Param("statuses") Collection<Integer> statuses, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);

    @Query("SELECT p.productId, p.productName, od.variantLabel, p.imageUrl, SUM(od.quantity), SUM(od.quantity * od.priceAtPurchase) " +
           "FROM OrderDetailEntity od " +
           "JOIN od.product p " +
           "JOIN od.order o " +
           "WHERE o.shop.shopId = :shopId " +
           "  AND o.status IN :statuses " +
           "  AND o.createdAt >= :startDate " +
           "  AND o.createdAt < :endDate " +
           "GROUP BY p.productId, p.productName, od.variantLabel, p.imageUrl " +
           "ORDER BY SUM(od.quantity * od.priceAtPurchase) DESC")
    List<Object[]> findTopRevenueProducts(
            @Param("shopId") Long shopId, 
            @Param("statuses") Collection<Integer> statuses, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
}
