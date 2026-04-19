package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.LoginDTO;
import com.example.backend_tmdt.dto.request.RegisterDTO;
import com.example.backend_tmdt.dto.response.LoginResponseDTO;
import com.example.backend_tmdt.entity.RefreshToken;
import com.example.backend_tmdt.entity.RoleEntity;
import com.example.backend_tmdt.entity.UserEntity;
import com.example.backend_tmdt.repository.RefreshTokenRepository;
import com.example.backend_tmdt.repository.RoleRepository;
import com.example.backend_tmdt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 7;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public String register(RegisterDTO request) {
        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }

        // 2. Khởi tạo User
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setStatus(1);

        // Băm mật khẩu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3. Cấp quyền mặc định ROLE_BUYER
        RoleEntity userRole = roleRepository.findByRoleName("ROLE_BUYER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Chưa khởi tạo ROLE_BUYER trong DB"));

        user.getRoles().add(userRole);

        // 4. Lưu xuống MySQL
        userRepository.save(user);

        return "Đăng ký tài khoản thành công!";
    }

    @Transactional
    public LoginResponseDTO login(LoginDTO request) {
        // Spring Security tự kiểm tra tài khoản & mật khẩu
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginKey(),
                        request.getPassword()
                )
        );

        // Lôi user từ DB
        UserEntity user = userRepository.findByLoginKey(request.getLoginKey())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng!"));

        // Sinh JWT access token và refresh token
        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createAndSaveRefreshToken(user);

        List<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());

        return LoginResponseDTO.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .roles(roleNames)
                .build();
    }

    @Transactional
    public LoginResponseDTO refreshToken(String refreshTokenValue) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ!"));

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token đã hết hạn, vui lòng đăng nhập lại!");
        }

        UserEntity user = storedToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        // Token rotation: cấp refresh token mới mỗi lần refresh
        storedToken.setToken(UUID.randomUUID().toString());
        storedToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRE_DAYS, ChronoUnit.DAYS));
        refreshTokenRepository.save(storedToken);

        List<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());

        return LoginResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(storedToken.getToken())
                .username(user.getUsername())
                .roles(roleNames)
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.deleteByToken(refreshTokenValue);
    }

    private RefreshToken createAndSaveRefreshToken(UserEntity user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> RefreshToken.builder().user(user).build());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRE_DAYS, ChronoUnit.DAYS));
        return refreshTokenRepository.save(refreshToken);
    }
}
