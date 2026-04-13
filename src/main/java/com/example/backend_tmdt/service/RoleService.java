package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.RoleEntity;
import com.example.backend_tmdt.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    public Optional<RoleEntity> findById(Long id) {
        return roleRepository.findById(id);
    }

    public RoleEntity save(RoleEntity entity) {
        return roleRepository.save(entity);
    }

    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }
}
