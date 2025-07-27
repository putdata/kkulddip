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
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * 액세스 토큰 생성
     *
     * @param username 사용자명 (토큰의 subject로 사용)
     * @param role 사용자 권한 (ADMIN, CUSTOMER, OWNER 중 하나)
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(String username, String role) {
        return generateToken(username, role, accessTokenExpiration);
    }

    /**
     * 리프레시 토큰 생성
     *
     * @param username 사용자명 (토큰의 subject로 사용)
     * @param role 사용자 권한 (ADMIN, CUSTOMER, OWNER 중 하나)
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(String username, String role) {
        return generateToken(username, role, refreshTokenExpiration);
    }

    /**
     * JWT 토큰 생성 (내부 메서드)
     * 공통 토큰 생성 로직을 처리합니다.
     * 
     * @param username 사용자명
     * @param role 사용자 권한
     * @param expiration 토큰 만료 시간 (초 단위)
     * @return 생성된 JWT 토큰
     */
    private String generateToken(String username, String role, long expiration) {
        Instant now = Instant.now();
        
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expiration, ChronoUnit.SECONDS)))
            .signWith(secretKey)
            .compact();
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
     * JWT 토큰 만료 여부 확인
     * 토큰의 만료 시간을 현재 시간과 비교하여 만료 여부를 판단합니다.
     * 
     * @param token JWT 토큰
     * @return 만료된 경우 true, 유효한 경우 false
     */
    public boolean isTokenExpired(String token) {
        try {
            return validateToken(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}