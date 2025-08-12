package com.kkulddip.stream.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 스트림 도메인 기본 예외 클래스
 * 스트림 관련 모든 예외의 부모 클래스입니다.
 */
public class StreamException extends BusinessException {
    
    public StreamException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public StreamException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public StreamException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}