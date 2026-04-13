package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.ProductEntity;
import com.example.backend_tmdt.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    public ProductEntity save(ProductEntity entity) {
        return productRepository.save(entity);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
