package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.UpdateUserRolesRequest;
import com.example.backend_tmdt.dto.request.UpdateUserStatusRequest;
import com.example.backend_tmdt.dto.response.ApiResponse;
import com.example.backend_tmdt.dto.response.UserListResponse;
import com.example.backend_tmdt.dto.response.UserResponse;
import com.example.backend_tmdt.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Danh sách người dùng (có pagination)
     * GET /api/admin/users?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserListResponse response = adminUserService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách người dùng thành công!"));
    }

    /**
     * Chi tiết một người dùng
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        UserResponse response = adminUserService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin người dùng thành công!"));
    }

    /**
     * Cập nhật trạng thái người dùng (khóa/mở khóa)
     * PUT /api/admin/users/{userId}/status
     * Body: { "status": 1 } (1=active, 0=inactive)
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        adminUserService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái người dùng thành công!"));
    }

    /**
     * Cập nhật vai trò người dùng
     * PUT /api/admin/users/{userId}/roles
     * Body: { "roles": ["ROLE_BUYER", "ROLE_SELLER"] }
     */
    @PutMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<Void>> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        adminUserService.updateUserRoles(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật vai trò người dùng thành công!"));
    }

    /**
     * Xóa (khóa) người dùng
     * DELETE /api/admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công!"));
    }
}
