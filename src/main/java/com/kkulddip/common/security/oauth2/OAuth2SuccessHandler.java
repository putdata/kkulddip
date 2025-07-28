package com.kkulddip.common.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.common.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하고 응답하는 핸들러
 * Google OAuth2 로그인 성공 후 JWT access/refresh 토큰을 생성하여 클라이언트에게 전달합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * OAuth2 로그인 성공 시 호출되는 메서드
     * JWT 토큰을 생성하고 JSON 응답으로 반환합니다.
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 정보
     * @throws IOException I/O 예외 발생 시
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2UserPrincipal userPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();

        String email = userPrincipal.getEmail();
        String role = userPrincipal.getRole();
        String provider = userPrincipal.getProvider();
        String providerId = userPrincipal.getProviderId();

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(email, role, provider, providerId);
        String refreshToken = jwtUtil.generateRefreshToken(email, role, provider, providerId);

        log.info("OAuth2 로그인 성공 - 사용자: {}, 역할: {}, 제공자: {}, 제공자ID: {}", email, role, provider, providerId);

        // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/login-success")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("tokenType", "Bearer")
                .queryParam("expiresIn", 3600)
                .queryParam("userId", userPrincipal.getUserId())
                .queryParam("email", userPrincipal.getEmail())
                .queryParam("name", userPrincipal.getUserName())
                .queryParam("role", userPrincipal.getRole())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
    
    /**
     * 토큰 응답 데이터 생성
     * 
     * @param accessToken JWT 액세스 토큰
     * @param refreshToken JWT 리프레시 토큰
     * @param userPrincipal 사용자 정보
     * @return 토큰 응답 데이터
     */
    private Map<String, Object> createTokenData(String accessToken, String refreshToken, 
                                              OAuth2UserPrincipal userPrincipal) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("accessToken", accessToken);
        tokenData.put("refreshToken", refreshToken);
        tokenData.put("tokenType", "Bearer");
        tokenData.put("expiresIn", 3600); // 1시간 (설정에서 가져올 수 있도록 개선)
        
        // 사용자 정보 추가
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userPrincipal.getUserId());
        userInfo.put("email", userPrincipal.getEmail());
        userInfo.put("name", userPrincipal.getUserName());
        userInfo.put("role", userPrincipal.getRole());
        userInfo.put("profileImageUrl", userPrincipal.getProfileImageUrl());
        userInfo.put("userType", userPrincipal.isCustomer() ? "CUSTOMER" : "OWNER");
        
        tokenData.put("user", userInfo);
        
        return tokenData;
    }
}