package com.kkulddip.common.security.oauth2.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

public class OAuth2JwtTokenCreationException extends BusinessException {

  public OAuth2JwtTokenCreationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
