package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.PaymentEntity;
import com.example.backend_tmdt.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<PaymentEntity> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<PaymentEntity> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public PaymentEntity save(PaymentEntity entity) {
        return paymentRepository.save(entity);
    }

    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }
}
