package com.kkulddip.common.security.oauth2.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 지원하지 않는 OAuth2 제공자 예외
 */
public class UnsupportedOAuth2ProviderException extends BusinessException {
    
    public UnsupportedOAuth2ProviderException(ErrorCode errorCode) {
        super(errorCode);
    }
}