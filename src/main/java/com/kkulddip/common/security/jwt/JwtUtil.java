package com.kkulddip.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * JWT 유틸리티 클래스 생성자
     * 
     * @param secretKey JWT 서명에 사용할 비밀키 (최소 256비트 이상)
     * @param accessTokenExpiration 액세스 토큰 만료 시간 (초 단위)
     * @param refreshTokenExpiration 리프레시 토큰 만료 시간 (초 단위)
     */
    public JwtUtil(
        @Value("${jwt.secret-key}") String secretKey,
        @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {

        if (secretKey == null || secretKey.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes)");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * 액세스 토큰 생성
     *
     * @param username 사용자명
     * @param role 사용자 권한
     * @param provider OAuth2 제공자 (null일 수 있음)
     * @param providerId OAuth2 제공자 ID (null일 수 있음)
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(String username, String role, String provider, String providerId) {
        return generateToken(username, role, "ACCESS", provider, providerId, accessTokenExpiration);
    }

    /**
     * 리프레시 토큰 생성
     *
     * @param username 사용자명
     * @param role 사용자 권한
     * @param provider OAuth2 제공자 (null일 수 있음)
     * @param providerId OAuth2 제공자 ID (null일 수 있음)
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(String username, String role, String provider, String providerId) {
        return generateToken(username, role, "REFRESH", provider, providerId, refreshTokenExpiration);
    }

    /**
     * JWT 토큰 생성 (내부 메서드)
     * 공통 토큰 생성 로직을 처리합니다.
     * 
     * @param username 사용자명
     * @param role 사용자 권한
     * @param provider OAuth2 제공자 (nullable)
     * @param providerId OAuth2 제공자 ID (nullable)
     * @param expiration 토큰 만료 시간 (초 단위)
     * @return 생성된 JWT 토큰
     */
    private String generateToken(String username, String role, String tokenType, String provider, String providerId, long expiration) {
        Instant now = Instant.now();
        
        var builder = Jwts.builder()
            .subject(username)
            .claim("role", role)
            .claim("token_type", tokenType)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expiration, ChronoUnit.SECONDS)));
            
        if (provider != null) {
            builder.claim("oauth2_provider", provider);
        }
        if (providerId != null) {
            builder.claim("oauth2_provider_id", providerId);
        }
        
        return builder.signWith(secretKey).compact();
    }

    /**
     * JWT 토큰 검증 및 클레임 추출
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰의 클레임 정보
     * @throws JwtException 토큰이 유효하지 않은 경우
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * JWT 토큰에서 사용자명 추출
     * 토큰의 subject 클레임에서 사용자명을 반환합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    /**
     * JWT 토큰에서 권한 추출
     * 토큰의 role 클레임에서 사용자 권한을 반환합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자 권한 (ADMIN, CUSTOMER, OWNER 중 하나)
     */
    public String extractRole(String token) {
        return validateToken(token).get("role", String.class);
    }
    
    /**
     * JWT 토큰에서 OAuth2 제공자 추출
     * 토큰의 oauth2_provider 클레임에서 OAuth2 제공자를 반환합니다.
     * 
     * @param token JWT 토큰
     * @return OAuth2 제공자 (google, kakao 등), OAuth2가 아닌 경우 null
     */
    public String extractOAuth2Provider(String token) {
        return validateToken(token).get("oauth2_provider", String.class);
    }
    
    /**
     * JWT 토큰에서 OAuth2 제공자 ID 추출
     * 토큰의 oauth2_provider_id 클레임에서 OAuth2 제공자 ID를 반환합니다.
     * 
     * @param token JWT 토큰
     * @return OAuth2 제공자 ID, OAuth2가 아닌 경우 null
     */
    public String extractOAuth2ProviderId(String token) {
        return validateToken(token).get("oauth2_provider_id", String.class);
    }
}