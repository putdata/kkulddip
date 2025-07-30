package com.kkulddip.common.security.oauth2.exception;

import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * OAuth2 관련 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class OAuth2ExceptionHandler {
    
    /**
     * OAuth2 인증 예외 처리
     */
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ErrorResponse<Void>> handleOAuth2AuthenticationException(OAuth2AuthenticationException ex) {
        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            ErrorCode.AUTH_OAUTH2_AUTHENTICATION_FAILED
        );

        log.warn("OAuth2 인증 실패: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                           .body(errorResponse);
    }
    
    /**
     * OAuth2 사용자 정보 처리 예외
     */
    @ExceptionHandler(OAuth2UserInfoException.class)
    public ResponseEntity<ErrorResponse<Void>> handleOAuth2UserInfoException(OAuth2UserInfoException ex) {
        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED
        );

        log.warn("OAuth2 사용자 정보 처리 실패: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                           .body(errorResponse);
    }
    
    /**
     * OAuth2 제공자 지원 안함 예외
     */
    @ExceptionHandler(OAuth2UnsupportedProviderException.class)
    public ResponseEntity<ErrorResponse<Void>> handleUnsupportedOAuth2ProviderException(OAuth2UnsupportedProviderException ex) {
        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            ErrorCode.AUTH_OAUTH2_UNSUPPORTED_PROVIDER
        );

        log.warn("지원하지 않는 OAuth2 제공자: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                           .body(errorResponse);
    }
}