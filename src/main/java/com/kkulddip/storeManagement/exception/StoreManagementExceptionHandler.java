package com.kkulddip.storeManagement.exception;

import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 가게 관리 도메인 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.kkulddip.storeManagement")
@Order(Ordered.HIGHEST_PRECEDENCE) // 글로벌 핸들러보다 우선 처리
public class StoreManagementExceptionHandler {

    /**
     * 가게 관리 도메인 비즈니스 예외 처리
     */
    @ExceptionHandler(StoreManagementException.class)
    public ResponseEntity<ApiResponse<Void>> handleStoreManagementException(StoreManagementException e) {
        log.warn("가게 관리 비즈니스 예외 발생: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        HttpStatus httpStatus = getHttpStatusFromErrorCode(e.getErrorCode());
        
        return ResponseEntity
            .status(httpStatus)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Bean Validation 예외 처리 (@Valid 어노테이션)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("요청 데이터 검증 실패: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, e.getBindingResult());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Bean Validation 예외 처리 (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.warn("요청 데이터 바인딩 실패: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, e.getBindingResult());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * IllegalArgumentException 처리 (Validator에서 발생)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("잘못된 인수 예외: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, e.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * 보안 관련 예외 처리 (접근 거부)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.warn("접근 권한 없음: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ACCESS_DENIED, "해당 작업에 대한 권한이 없습니다.");
        
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * 인증 관련 예외 처리
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(org.springframework.security.core.AuthenticationException e) {
        log.warn("인증 실패: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.AUTH_AUTHENTICATION_FAILED, "인증이 필요합니다.");
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상하지 못한 예외 발생", e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            ErrorCode.INTERNAL_SERVER_ERROR, 
            "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * ErrorCode를 HttpStatus로 변환
     */
    private HttpStatus getHttpStatusFromErrorCode(ErrorCode errorCode) {
        return switch (errorCode) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case AUTH_AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case STORE_NOT_FOUND, RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_RESOURCE -> HttpStatus.CONFLICT;
            case BUSINESS_LOGIC_ERROR -> HttpStatus.CONFLICT;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}