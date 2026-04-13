package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.CartEntity;
import com.example.backend_tmdt.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public List<CartEntity> findAll() {
        return cartRepository.findAll();
    }

    public Optional<CartEntity> findById(Long id) {
        return cartRepository.findById(id);
    }

    public CartEntity save(CartEntity entity) {
        return cartRepository.save(entity);
    }

    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }
}
