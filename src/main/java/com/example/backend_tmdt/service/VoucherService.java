package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.VoucherEntity;
import com.example.backend_tmdt.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public List<VoucherEntity> findAll() {
        return voucherRepository.findAll();
    }

    public Optional<VoucherEntity> findById(Long id) {
        return voucherRepository.findById(id);
    }

    public VoucherEntity save(VoucherEntity entity) {
        return voucherRepository.save(entity);
    }

    public void deleteById(Long id) {
        voucherRepository.deleteById(id);
    }
}
