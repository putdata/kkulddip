package com.kkulddip.common.security.exception;

import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Spring Security 관련 예외를 처리하는 핸들러
 * JWT 인증/인가와 관련된 모든 예외를 중앙에서 처리합니다.
 */
@Slf4j
@RestControllerAdvice
@Order(1) // GlobalExceptionHandler보다 우선순위를 높게 설정
public class SecurityExceptionHandler {

    /**
     * Spring Security 인증 예외 처리
     * 인증이 필요한 리소스에 인증 없이 접근하거나 토큰이 유효하지 않을 때 발생합니다.
     * 
     * @param e AuthenticationException
     * @return 401 Unauthorized 에러 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("AuthenticationException: {}", e.getMessage());
        
        ErrorCode errorCode = ErrorCode.AUTH_UNAUTHORIZED;
        ErrorResponse<Void> response = ErrorResponse.of(errorCode);
        
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * Spring Security 접근 거부 예외 처리
     * @PreAuthorize 등에서 권한 검증 실패 시 발생합니다.
     * 
     * @param e AccessDeniedException
     * @return 403 Forbidden 에러 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("AccessDeniedException: {}", e.getMessage());
        
        ErrorCode errorCode = ErrorCode.AUTH_ACCESS_DENIED;
        ErrorResponse<Void> response = ErrorResponse.of(errorCode);
        
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}