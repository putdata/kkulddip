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
@Component
@RequiredArgsConstructor
public class JwtAuthenticationErrorHandler {

    private final ObjectMapper objectMapper;

    /**
     * JWT 토큰이 유효하지 않은 경우 에러 응답
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

//    /**
//     * JWT 토큰이 없거나 인증이 실패한 경우 에러 응답
//     *
//     * @param response HTTP 응답 객체
//     * @param message  에러 메시지
//     * @throws IOException 응답 작성 중 I/O 오류 발생 시
//     */
//    public void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
//        log.warn("인증이 실패했습니다: {}", message);
//        writeErrorResponse(response, ErrorCode.AUTH_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
//    }

//    /**
//     * 접근 권한이 없는 경우 에러 응답
//     *
//     * @param response HTTP 응답 객체
//     * @param message  에러 메시지
//     * @throws IOException 응답 작성 중 I/O 오류 발생 시
//     */
//    public void handleAccessDenied(HttpServletResponse response, String message) throws IOException {
//        log.warn("접근이 거부되었습니다: {}", message);
//        writeErrorResponse(response, ErrorCode.AUTH_ACCESS_DENIED, HttpStatus.FORBIDDEN);
//    }

    /**
     * 공통 에러 응답 작성 메서드
     * HTTP 응답에 JSON 형태의 표준화된 에러 정보를 작성합니다.
     *
     * @param response  HTTP 응답 객체
     * @param errorCode 에러 코드
     * @param status    HTTP 상태 코드
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