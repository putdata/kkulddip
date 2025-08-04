package com.kkulddip.common.security.config;

/**
 * 보안 설정에서 사용되는 공통 경로 상수를 정의하는 클래스
 * SecurityConfig와 JwtAuthenticationFilter에서 동일한 경로 상수를 사용하여
 * 일관성을 보장하고 유지보수성을 향상시킵니다.
 */
public final class SecurityPaths {

    private SecurityPaths() {
        // 유틸리티 클래스의 인스턴스화 방지
    }

    /**
     * 인증 없이 접근 가능한 모든 HTTP 메서드 경로
     */
    public static final String[] PUBLIC_PATHS = {
        "/api/v1/auth/**",
        "/swagger-ui/**",
        "/api-docs/**"
    };

    /**
     * GET 메서드로만 인증 없이 접근 가능한 경로
     */
    public static final String[] PUBLIC_GET_PATHS = {
        "/api/v1/events/**",
        "/api/v1/stores/**"
    };
}