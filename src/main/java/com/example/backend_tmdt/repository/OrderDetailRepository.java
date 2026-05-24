package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    List<OrderDetailEntity> findByOrderOrderId(Long orderId);
}
