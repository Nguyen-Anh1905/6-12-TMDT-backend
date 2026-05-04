package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Find by status and approval
    Page<ProductEntity> findByStatusAndIsApproved(Integer status, Boolean isApproved, Pageable pageable);

        // Find by category.categoryId, status and approval
        Page<ProductEntity> findByCategoryCategoryIdAndStatusAndIsApproved(Long categoryId, Integer status, Boolean isApproved, Pageable pageable);

        Page<ProductEntity> findByCategoryCategoryIdInAndStatusAndIsApproved(Collection<Long> categoryIds, Integer status, Boolean isApproved, Pageable pageable);

        // Find by shop.shopId, status and approval
        Page<ProductEntity> findByShopShopIdAndStatusAndIsApproved(Long shopId, Integer status, Boolean isApproved, Pageable pageable);

    // Find pending products (not approved)
    Page<ProductEntity> findByIsApproved(Boolean isApproved, Pageable pageable);

    // Search by keyword, price range, status and approval
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
            "p.price BETWEEN :minPrice AND :maxPrice AND " +
            "p.status = :status AND p.isApproved = :isApproved")
    Page<ProductEntity> searchProductsByFilters(
            @Param("keyword") String keyword,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("status") Integer status,
            @Param("isApproved") Boolean isApproved,
            Pageable pageable
    );

    // Search by keyword, category, price range, status and approval
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
            "p.category.categoryId = :categoryId AND " +
            "p.price BETWEEN :minPrice AND :maxPrice AND " +
            "p.status = :status AND p.isApproved = :isApproved")
    Page<ProductEntity> searchProductsByCategoryAndFilters(
            @Param("keyword") String keyword,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("isApproved") Boolean isApproved,
            Pageable pageable
    );

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
            "p.category.categoryId IN :categoryIds AND " +
            "p.price BETWEEN :minPrice AND :maxPrice AND " +
            "p.status = :status AND p.isApproved = :isApproved")
    Page<ProductEntity> searchProductsByCategoryIdsAndFilters(
            @Param("keyword") String keyword,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("categoryIds") Collection<Long> categoryIds,
            @Param("status") Integer status,
            @Param("isApproved") Boolean isApproved,
            Pageable pageable
    );

    // Find all products by shop ID (for seller dashboard)
    Page<ProductEntity> findByShopShopId(Long shopId, Pageable pageable);

        // Find seller products excluding soft-deleted items
        Page<ProductEntity> findByShopShopIdAndStatusNot(Long shopId, Integer status, Pageable pageable);
}
