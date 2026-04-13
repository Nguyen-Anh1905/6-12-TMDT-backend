package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.ReplyEntity;
import com.example.backend_tmdt.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    public List<ReplyEntity> findAll() {
        return replyRepository.findAll();
    }

    public Optional<ReplyEntity> findById(Long id) {
        return replyRepository.findById(id);
    }

    public ReplyEntity save(ReplyEntity entity) {
        return replyRepository.save(entity);
    }

    public void deleteById(Long id) {
        replyRepository.deleteById(id);
    }
}
