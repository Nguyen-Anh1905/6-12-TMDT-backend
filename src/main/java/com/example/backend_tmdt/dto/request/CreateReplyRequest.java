package com.example.backend_tmdt.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReplyRequest {
    private String content;
}
