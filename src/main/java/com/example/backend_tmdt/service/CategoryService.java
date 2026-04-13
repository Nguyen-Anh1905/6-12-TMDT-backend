package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.CategoryEntity;
import com.example.backend_tmdt.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryEntity> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<CategoryEntity> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public CategoryEntity save(CategoryEntity entity) {
        return categoryRepository.save(entity);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
