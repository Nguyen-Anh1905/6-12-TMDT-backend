package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.OrderDetailEntity;
import com.example.backend_tmdt.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public List<OrderDetailEntity> findAll() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetailEntity> findById(Long id) {
        return orderDetailRepository.findById(id);
    }

    public OrderDetailEntity save(OrderDetailEntity entity) {
        return orderDetailRepository.save(entity);
    }

    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }
}
