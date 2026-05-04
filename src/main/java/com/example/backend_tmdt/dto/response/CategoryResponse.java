package com.example.backend_tmdt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private Integer moderationLevel;
    private Long parentId;
}
