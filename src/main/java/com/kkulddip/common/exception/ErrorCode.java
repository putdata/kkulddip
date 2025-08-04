package com.kkulddip.common.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {
    
    // 공통 도메인 에러
    COMMON_INVALID_INPUT(BAD_REQUEST, "COMMON_INVALID_INPUT", "잘못된 입력값입니다."),
    COMMON_INVALID_TYPE(BAD_REQUEST, "COMMON_INVALID_TYPE", "잘못된 타입입니다."),
    COMMON_NOT_FOUND(NOT_FOUND, "COMMON_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    COMMON_METHOD_NOT_ALLOWED(METHOD_NOT_ALLOWED, "COMMON_METHOD_NOT_ALLOWED", "허용되지 않는 메서드입니다."),
    COMMON_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON_INTERNAL_SERVER_ERROR", "내부 서버 오류입니다."),
    
    // 사용자 도메인 에러
    USER_REGISTER_TYPE_ERROR(BAD_REQUEST, "USER_REGISTER_TYPE_ERROR", "사용자 등록 타입 오류입니다."),
    USER_NOT_FOUND(NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_DUPLICATE_EMAIL(CONFLICT, "USER_DUPLICATE_EMAIL", "이미 존재하는 이메일입니다."),
    USER_INVALID_PASSWORD(BAD_REQUEST, "USER_INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    DUPLICATE_PHONE_NUMBER(CONFLICT, "DUPLICATE_PHONE_NUMBER", "이미 존재하는 전화번호입니다."),
    
    // 인증 도메인 에러
    AUTH_UNAUTHORIZED(UNAUTHORIZED, "AUTH_UNAUTHORIZED", "인증이 필요합니다."),
    AUTH_INVALID_TOKEN(UNAUTHORIZED, "AUTH_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(UNAUTHORIZED, "AUTH_EXPIRED_TOKEN", "만료된 토큰입니다."),
    AUTH_ACCESS_DENIED(FORBIDDEN, "AUTH_ACCESS_DENIED", "접근이 거부되었습니다."),
    
    // 주문 도메인 에러
    ORDER_NOT_FOUND(NOT_FOUND, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    ORDER_CANNOT_BE_MODIFIED(BAD_REQUEST, "ORDER_CANNOT_BE_MODIFIED", "주문을 수정할 수 없습니다."),
    ORDER_CANNOT_BE_CANCELLED(BAD_REQUEST, "ORDER_CANNOT_BE_CANCELLED", "주문을 취소할 수 없습니다."),
    ORDER_CANNOT_BE_CONFIRMED(BAD_REQUEST, "ORDER_CANNOT_BE_CONFIRMED", "주문을 확인할 수 없습니다."),
    ORDER_CANNOT_BE_DELIVERED(BAD_REQUEST, "ORDER_CANNOT_BE_DELIVERED", "주문을 배송 완료 처리할 수 없습니다."),
    ORDER_INVALID_STATUS(BAD_REQUEST, "ORDER_INVALID_STATUS", "잘못된 주문 상태입니다."),
    ORDER_EMPTY_ITEMS(BAD_REQUEST, "ORDER_EMPTY_ITEMS", "주문 항목이 비어있습니다."),

    // 알림 도메인 에러
    NOTIFICATION_NOT_FOUND(NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 