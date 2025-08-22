package com.kkulddip.common.security.oauth2.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * OAuth2 사용자 정보 처리 관련 예외
 */
public class OAuth2UserInfoException extends BusinessException {
    
    public OAuth2UserInfoException(ErrorCode errorCode) {
        super(errorCode);
    }
}