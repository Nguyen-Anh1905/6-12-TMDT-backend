package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    // Tìm role theo tên (ví dụ: "ROLE_BUYER", "ROLE_SELLER", "ROLE_ADMIN")
    Optional<RoleEntity> findByRoleName(String roleName);
}
