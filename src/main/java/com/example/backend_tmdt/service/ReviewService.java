package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.ReviewEntity;
import com.example.backend_tmdt.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewEntity> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<ReviewEntity> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public ReviewEntity save(ReviewEntity entity) {
        return reviewRepository.save(entity);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}
