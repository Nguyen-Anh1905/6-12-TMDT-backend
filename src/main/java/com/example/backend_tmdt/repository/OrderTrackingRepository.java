package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.OrderTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTrackingEntity, Long> {
}
