package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.UserEntity;
import com.example.backend_tmdt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserEntity save(UserEntity entity) {
        return userRepository.save(entity);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
