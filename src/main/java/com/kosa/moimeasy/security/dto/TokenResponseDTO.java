package com.kosa.moimeasy.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private String nickname;
    private Long roleId; // long으로 줘야하나?
    private String accessToken;
    private String refreshToken;
}
