package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.RefreshToken;
import com.example.backend_tmdt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(UserEntity user);

    // Xóa token cũ của user (khi logout hoặc đăng nhập lại)
    @Modifying
    int deleteByUser(UserEntity user);

    @Modifying
    int deleteByToken(String token);
}
