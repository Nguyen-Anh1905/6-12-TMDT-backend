package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Integer status;
    private List<String> roles;
    private String shopName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
