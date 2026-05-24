package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse {
    private List<UserResponse> content;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Integer pageSize;
}
