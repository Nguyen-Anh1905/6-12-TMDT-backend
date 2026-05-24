package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {

    List<VoucherEntity> findByShopShopIdOrderByCreatedAtDesc(Long shopId);

    Optional<VoucherEntity> findByVoucherIdAndShopShopId(Long voucherId, Long shopId);

    boolean existsByCode(String code);

    Optional<VoucherEntity> findByCodeAndShopShopId(String code, Long shopId);
}
