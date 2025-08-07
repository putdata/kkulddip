package com.kkulddip.store.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 가게를 찾을 수 없을 때 발생하는 예외
 */
public class StoreNotFoundException extends BusinessException {

    public StoreNotFoundException() {
        super(ErrorCode.STORE_NOT_FOUND);
    }

    public StoreNotFoundException(Long storeId) {
        super(ErrorCode.STORE_NOT_FOUND, "가게 ID: " + storeId);
    }

    public StoreNotFoundException(String message) {
        super(ErrorCode.STORE_NOT_FOUND, message);
    }
}