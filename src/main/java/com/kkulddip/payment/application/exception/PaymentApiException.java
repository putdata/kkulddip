package com.kkulddip.payment.application.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

public class PaymentApiException extends BusinessException {

    public PaymentApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PaymentApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}