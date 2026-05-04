package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.response.CategoryResponse;
import com.example.backend_tmdt.entity.CategoryEntity;
import com.example.backend_tmdt.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> responses = categoryRepository.findAllByOrderByParentCategoryCategoryIdAscCategoryNameAsc()
                .stream()
                .map(this::toCategoryResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private CategoryResponse toCategoryResponse(CategoryEntity category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .moderationLevel(category.getModerationLevel())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null)
                .build();
    }
}
