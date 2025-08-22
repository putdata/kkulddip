package com.kkulddip.common.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 인증 관련 에러 처리를 담당하는 핸들러
 * Spring Security 필터 체인에서 발생하는 JWT 관련 예외들을 처리
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationErrorHandler {

    private final ObjectMapper objectMapper;

    /**
     * JWT 토큰이 요청에 없는 경우 에러 응답
     *
     * @param response HTTP 응답 객체
     * @param requestUri 요청 URI
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleMissingToken(HttpServletResponse response, String requestUri) throws IOException {
        log.warn("JWT 토큰이 없습니다. URI: {}", requestUri);
        writeErrorResponse(response, ErrorCode.AUTH_TOKEN_MISSING, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Authorization 헤더 형식이 잘못된 경우 에러 응답
     *
     * @param response HTTP 응답 객체
     * @param authHeader Authorization 헤더 값
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleInvalidAuthHeader(HttpServletResponse response, String authHeader) throws IOException {
        log.warn("Authorization 헤더 형식이 잘못되었습니다: {}", authHeader);
        writeErrorResponse(response, ErrorCode.AUTH_INVALID_HEADER_FORMAT, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰이 만료된 경우 에러 응답
     *
     * @param response HTTP 응답 객체
     * @param message  에러 메시지
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleExpiredToken(HttpServletResponse response, String message) throws IOException {
        log.warn("JWT 토큰이 만료되었습니다: {}", message);
        writeErrorResponse(response, ErrorCode.AUTH_EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰 서명이 유효하지 않은 경우 에러 응답
     *
     * @param response HTTP 응답 객체
     * @param message  에러 메시지
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleInvalidSignature(HttpServletResponse response, String message) throws IOException {
        log.warn("JWT 토큰 서명이 유효하지 않습니다: {}", message);
        writeErrorResponse(response, ErrorCode.AUTH_INVALID_SIGNATURE, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰 형식이 잘못된 경우 에러 응답
     * (잘못된 구조, 파싱 불가능한 토큰)
     *
     * @param response HTTP 응답 객체
     * @param message  에러 메시지
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleMalformedToken(HttpServletResponse response, String message) throws IOException {
        log.warn("JWT 토큰 형식이 잘못되었습니다: {}", message);
        writeErrorResponse(response, ErrorCode.AUTH_MALFORMED_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰의 필수 클레임이 없는 경우 에러 응답
     * (subject가 없거나 필수 정보 누락)
     *
     * @param response HTTP 응답 객체
     * @param missingClaim 누락된 클레임명
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleMissingRequiredClaim(HttpServletResponse response, String missingClaim) throws IOException {
        log.warn("JWT 토큰에 필수 클레임이 없습니다: {}", missingClaim);
        writeErrorResponse(response, ErrorCode.AUTH_MISSING_REQUIRED_CLAIM, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰이 유효하지 않은 경우 에러 응답 (일반적인 경우)
     *
     * @param response HTTP 응답 객체
     * @param message  에러 메시지
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        log.warn("JWT 토큰이 유효하지 않습니다: {}", message);
        writeErrorResponse(response, ErrorCode.AUTH_INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰 처리 중 예상치 못한 오류 발생 시 에러 응답
     *
     * @param response HTTP 응답 객체
     * @param throwable 발생한 예외
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    public void handleUnexpectedError(HttpServletResponse response, Throwable throwable) throws IOException {
        log.error("JWT 토큰 처리 중 예상치 못한 오류가 발생했습니다", throwable);
        writeErrorResponse(response, ErrorCode.COMMON_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 공통 에러 응답 작성 메서드
     * HTTP 응답에 JSON 형태의 표준화된 에러 정보를 작성합니다.
     *
     * @param response HTTP 응답 객체
     * @param errorCode 에러 코드
     * @param status HTTP 상태 코드
     * @throws IOException 응답 작성 중 I/O 오류 발생 시
     */
    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse<Void> errorResponse = ErrorResponse.of(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}