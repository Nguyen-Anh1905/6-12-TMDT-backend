package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.CartProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProductEntity, Long> {

    List<CartProductEntity> findByCartCartId(Long cartId);

    Optional<CartProductEntity> findByCartCartIdAndProductProductId(Long cartId, Long productId);

    Optional<CartProductEntity> findByCartCartIdAndProductProductIdAndVariantLabel(Long cartId, Long productId, String variantLabel);

    void deleteByCartCartIdAndProductProductId(Long cartId, Long productId);
}
