package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.CartProductEntity;
import com.example.backend_tmdt.repository.CartProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartProductService {

    private final CartProductRepository cartProductRepository;

    public List<CartProductEntity> findAll() {
        return cartProductRepository.findAll();
    }

    public Optional<CartProductEntity> findById(Long id) {
        return cartProductRepository.findById(id);
    }

    public CartProductEntity save(CartProductEntity entity) {
        return cartProductRepository.save(entity);
    }

    public void deleteById(Long id) {
        cartProductRepository.deleteById(id);
    }
}
