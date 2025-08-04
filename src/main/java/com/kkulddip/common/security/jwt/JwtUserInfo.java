package com.kkulddip.common.security.jwt;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.oauth2.exception.OAuth2JwtTokenCreationException;

import java.util.Optional;

/**
 * JWT 사용자 정보를 담는 레코드 클래스
 * 필수 필드에 대한 검증을 수행합니다.
 */
public record JwtUserInfo(
    String username,
    String role,
    String oauth2Provider,
    String oauth2ProviderId
) {
    public JwtUserInfo {
        validateRequired(username, "Username");
        validateRequired(role, "Role");
        validateRequired(oauth2Provider, "OAuth2 provider");
        validateRequired(oauth2ProviderId, "OAuth2 provider ID");
    }

    private static void validateRequired(String value, String fieldName) {
        Optional.ofNullable(value)
                .filter(s -> !s.trim().isEmpty())
                .orElseThrow(() -> new OAuth2JwtTokenCreationException(ErrorCode.AUTH_MISSING_REQUIRED_CLAIM));
    }
}