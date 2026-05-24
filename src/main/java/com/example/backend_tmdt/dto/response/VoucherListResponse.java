package com.example.backend_tmdt.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherListResponse {
    private List<VoucherResponse> content;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Integer pageSize;
}
