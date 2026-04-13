package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.PermissionEntity;
import com.example.backend_tmdt.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionEntity> findAll() {
        return permissionRepository.findAll();
    }

    public Optional<PermissionEntity> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public PermissionEntity save(PermissionEntity entity) {
        return permissionRepository.save(entity);
    }

    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
}
