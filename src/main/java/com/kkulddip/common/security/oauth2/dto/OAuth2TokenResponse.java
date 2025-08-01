package com.kkulddip.common.security.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkulddip.domain.user.entity.User;
import lombok.Builder;

/**
 * JWT 토큰 응답 DTO
 *
 * refresh token은 응답이 아닌 쿠키 설정으로 변경 필요
 */
@Builder
public record OAuth2TokenResponse(
    @JsonProperty("access_token")
    String accessToken,
    
    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("expires_in")
    long expiresIn,
    
    UserInfo user
) {
    @Builder
    public record UserInfo(
        String email,
        String name,
        String role,
        String profileImageUrl
    ) {
    }

}