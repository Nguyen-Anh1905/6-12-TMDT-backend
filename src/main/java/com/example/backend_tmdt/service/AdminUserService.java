package com.example.backend_tmdt.service;

import com.example.backend_tmdt.dto.request.UpdateUserRolesRequest;
import com.example.backend_tmdt.dto.request.UpdateUserStatusRequest;
import com.example.backend_tmdt.dto.response.UserListResponse;
import com.example.backend_tmdt.dto.response.UserResponse;
import com.example.backend_tmdt.entity.RoleEntity;
import com.example.backend_tmdt.entity.UserEntity;
import com.example.backend_tmdt.repository.RoleRepository;
import com.example.backend_tmdt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Lấy danh sách users với pagination
     * @param page Trang (0-based)
     * @param size Số users trên 1 trang
     * @return UserListResponse
     */
    public UserListResponse getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserResponse> content = users.getContent().stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());

        return UserListResponse.builder()
                .content(content)
                .currentPage(page)
                .pageSize(size)
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .build();
    }

    /**
     * Lấy chi tiết 1 user
     * @param userId ID của user
     * @return UserResponse
     */
    public UserResponse getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        return entityToResponse(user);
    }

    /**
     * Cập nhật trạng thái user (khóa/mở khóa)
     * @param userId ID của user
     * @param request Yêu cầu cập nhật (status: 1=active, 0=inactive)
     */
    @Transactional
    public void updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Không được khóa chính admin (optional check, tuỳ yêu cầu)
        if (user.getUserId().equals(1L) && request.getStatus() == 0) {
            throw new RuntimeException("Không thể khóa tài khoản admin chính!");
        }

        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    /**
     * Cập nhật roles của user
     * @param userId ID của user
     * @param request Danh sách roles (ví dụ: ["ROLE_BUYER", "ROLE_SELLER"])
     */
    @Transactional
    public void updateUserRoles(Long userId, UpdateUserRolesRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Lấy các role từ DB
        Set<RoleEntity> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            RoleEntity role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' không tồn tại!"));
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    /**
     * Xóa user (soft delete: set status = 0)
     * @param userId ID của user
     */
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Không được xóa admin chính
        if (user.getUserId().equals(1L)) {
            throw new RuntimeException("Không thể xóa tài khoản admin chính!");
        }

        // Soft delete: set status = 0
        user.setStatus(0);
        userRepository.save(user);
    }

    /**
     * Helper: Chuyển UserEntity thành UserResponse
     */
    private UserResponse entityToResponse(UserEntity user) {
        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
