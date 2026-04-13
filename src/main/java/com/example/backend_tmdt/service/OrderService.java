package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.OrderEntity;
import com.example.backend_tmdt.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    public Optional<OrderEntity> findById(Long id) {
        return orderRepository.findById(id);
    }

    public OrderEntity save(OrderEntity entity) {
        return orderRepository.save(entity);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
