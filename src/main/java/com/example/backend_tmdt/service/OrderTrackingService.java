package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.OrderTrackingEntity;
import com.example.backend_tmdt.repository.OrderTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderTrackingService {

    private final OrderTrackingRepository orderTrackingRepository;

    public List<OrderTrackingEntity> findAll() {
        return orderTrackingRepository.findAll();
    }

    public Optional<OrderTrackingEntity> findById(Long id) {
        return orderTrackingRepository.findById(id);
    }

    public OrderTrackingEntity save(OrderTrackingEntity entity) {
        return orderTrackingRepository.save(entity);
    }

    public void deleteById(Long id) {
        orderTrackingRepository.deleteById(id);
    }
}
