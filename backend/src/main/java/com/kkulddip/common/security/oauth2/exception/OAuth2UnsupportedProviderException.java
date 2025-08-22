package com.kkulddip.common.security.oauth2.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 지원하지 않는 OAuth2 제공자 예외
 */
public class OAuth2UnsupportedProviderException extends BusinessException {
    
    public OAuth2UnsupportedProviderException(ErrorCode errorCode) {
        super(errorCode);
    }
}