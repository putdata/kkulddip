package com.kkulddip.common.security.filter;

import com.kkulddip.common.security.exception.JwtAuthenticationErrorHandler;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.common.security.jwt.JwtUtil;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.security.SignatureException;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.kkulddip.common.security.config.SecurityPaths.PUBLIC_GET_PATHS;
import static com.kkulddip.common.security.config.SecurityPaths.PUBLIC_PATHS;

/**
 * JWT 기반 인증을 처리하는 필터 클래스
 *
 * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하고 검증하여
 * Spring Security의 SecurityContext에 인증 정보를 설정합니다.
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행됩니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

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

        try {
            if (!validateAuthHeader(request, response)) {
                return;
            }

            String token = extractTokenFromRequest(request);

            Claims tokenClaims = jwtUtil.extractClaims(token);

            if (!validateAccessToken(tokenClaims, response)) {
                return;
            }

            JwtUserInfo userInfo = jwtUtil.createJwtUserInfo(tokenClaims);

            UsernamePasswordAuthenticationToken authentication = createAuthentication(userInfo);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (MissingClaimException e) {
            errorHandler.handleMissingRequiredClaim(response, e.getClaimName());
            return;
        } catch (ExpiredJwtException e) {
            errorHandler.handleExpiredToken(response, e.getMessage());
            return;
        } catch (SignatureException e) {
            errorHandler.handleInvalidSignature(response, e.getMessage());
            return;
        } catch (MalformedJwtException e) {
            errorHandler.handleMalformedToken(response, e.getMessage());
            return;
        } catch (JwtException e) {
            errorHandler.handleInvalidToken(response, e.getMessage());
            return;
        } catch (Exception e) {
            errorHandler.handleUnexpectedError(response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * JWT 인증 필터를 건너뛸 요청 경로 판단
     * 특정 공개 API 엔드포인트에 대해서는 JWT 인증을 수행하지 않습니다.
     * 
     * @param request HTTP 요청 객체
     * @return 필터를 건너뛸 경우 true, JWT 인증이 필요한 경우 false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        boolean isPublicPath = Arrays.stream(PUBLIC_PATHS)
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
        boolean isPublicGetPath = HTTPRequest.Method.GET.equals(method) && Arrays.stream(PUBLIC_GET_PATHS)
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
            
        return isPublicPath || isPublicGetPath;
    }

    /**
     * JWT 사용자 정보를 기반으로 Spring Security Authentication 객체 생성
     * 사용자 정보와 권한을 포함한 인증 토큰을 생성하고, OAuth2 관련 상세 정보를 설정합니다.
     *
     * @param userInfo JWT에서 추출된 사용자 정보
     * @return Spring Security에서 사용할 Authentication 객체
     */
    private UsernamePasswordAuthenticationToken createAuthentication(JwtUserInfo userInfo) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userInfo.username(),
            null,
            Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + userInfo.role()))
        );

        Map<String, Object> details = new HashMap<>();
        details.put("oauth2_provider", userInfo.oauth2Provider());
        details.put("oauth2_provider_id", userInfo.oauth2ProviderId());
        authentication.setDetails(details);

        return authentication;
    }

    /**
     * Authorization 헤더 사전 검증
     * 토큰 존재 여부와 형식을 검증하고, 문제가 있으면 에러 응답 후 false 반환
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 검증 통과 시 true, 실패 시 false
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    private boolean validateAuthHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authHeader)) {
            errorHandler.handleMissingToken(response, request.getRequestURI());
            return false;
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            errorHandler.handleInvalidAuthHeader(response, authHeader);
            return false;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!StringUtils.hasText(token)) {
            errorHandler.handleInvalidAuthHeader(response, authHeader);
            return false;
        }

        return true;
    }

    /**
     * JWT 토큰의 타입이 ACCESS 토큰인지 검증
     * REFRESH 토큰이나 다른 타입의 토큰은 거부합니다.
     *
     * @param tokenClaims JWT 토큰에서 추출한 클레임
     * @param response HTTP 응답 객체
     * @return 검증 통과 시 true, 실패 시 false
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    private boolean validateAccessToken(Claims tokenClaims, HttpServletResponse response) throws IOException {
        String tokenType = tokenClaims.get("token_type", String.class);

        if ("ACCESS".equals(tokenType)) {
            return true;
        }

        errorHandler.handleInvalidToken(response, "ACCESS 토큰이 아닙니다: " + tokenType);
        return false;
    }

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출
     * 사전 검증이 완료된 상태에서 순수하게 토큰만 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER)
            .substring(BEARER_PREFIX.length());
    }
}