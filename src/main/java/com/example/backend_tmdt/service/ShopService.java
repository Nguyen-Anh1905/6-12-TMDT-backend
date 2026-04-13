package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public List<ShopEntity> findAll() {
        return shopRepository.findAll();
    }

    public Optional<ShopEntity> findById(Long id) {
        return shopRepository.findById(id);
    }

    public ShopEntity save(ShopEntity entity) {
        return shopRepository.save(entity);
    }

    public void deleteById(Long id) {
        shopRepository.deleteById(id);
    }
}
