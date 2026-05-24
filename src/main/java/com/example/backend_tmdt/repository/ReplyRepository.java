package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    Optional<ReplyEntity> findFirstByReviewReviewId(Long reviewId);
}
