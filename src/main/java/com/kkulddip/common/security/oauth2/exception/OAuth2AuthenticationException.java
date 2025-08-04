package com.kkulddip.common.security.oauth2.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * OAuth2 인증 관련 예외
 */
public class OAuth2AuthenticationException extends BusinessException {
    
    public OAuth2AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}