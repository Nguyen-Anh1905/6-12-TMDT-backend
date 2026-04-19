package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Dùng cho register
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    // Dùng cho login (username, email hoặc phone)
    @Query("SELECT u FROM UserEntity u WHERE u.username = :key OR u.email = :key OR u.phone = :key")
    Optional<UserEntity> findByLoginKey(@Param("key") String key);
}
