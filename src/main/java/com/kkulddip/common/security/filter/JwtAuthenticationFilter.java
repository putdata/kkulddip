package com.kkulddip.common.security.filter;

import com.kkulddip.common.security.exception.JwtAuthenticationErrorHandler;
import com.kkulddip.common.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 기반 인증을 처리하는 필터 클래스
 *
 * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하고 검증하여
 * Spring Security의 SecurityContext에 인증 정보를 설정합니다.
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationErrorHandler errorHandler;

    /**
     * 각 HTTP 요청에 대해 JWT 인증을 처리하는 메인 메서드
     * Authorization 헤더에서 JWT 토큰을 추출하고 검증하여 인증 정보를 설정합니다.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외 발생 시
     * @throws IOException I/O 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            // 인증 정보 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username, null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT authentication successful for user: {}", username);

        } catch (ExpiredJwtException e) {
            errorHandler.handleExpiredToken(response, e.getMessage());
            return;
        } catch (JwtException e) {
            errorHandler.handleInvalidToken(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출
     * Bearer 토큰 형식("Bearer <token>")에서 실제 토큰 부분만 반환합니다.
     * 
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 또는 null (토큰이 없거나 형식이 잘못된 경우)
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}