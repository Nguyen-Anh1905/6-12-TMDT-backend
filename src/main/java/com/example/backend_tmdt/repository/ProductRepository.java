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

    Page<ProductEntity> findByStatusAndIsApproved(Integer status, Boolean isApproved, Pageable pageable);

    Page<ProductEntity> findByCategoryCategoryIdAndStatusAndIsApproved(Long categoryId, Integer status, Boolean isApproved, Pageable pageable);

    Page<ProductEntity> findByCategoryCategoryIdInAndStatusAndIsApproved(Collection<Long> categoryIds, Integer status, Boolean isApproved, Pageable pageable);

    Page<ProductEntity> findByShopShopIdAndStatusAndIsApproved(Long shopId, Integer status, Boolean isApproved, Pageable pageable);

    Page<ProductEntity> findByIsApproved(Boolean isApproved, Pageable pageable);

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

    Page<ProductEntity> findByShopShopId(Long shopId, Pageable pageable);

    Page<ProductEntity> findByShopShopIdAndStatusNot(Long shopId, Integer status, Pageable pageable);

    long countByShopShopId(Long shopId);

    long countByShopShopIdAndStatus(Long shopId, Integer status);

    long countByShopShopIdAndIsApproved(Long shopId, Boolean isApproved);
}
