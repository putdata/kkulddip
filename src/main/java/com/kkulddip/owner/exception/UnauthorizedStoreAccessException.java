package com.kkulddip.owner.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

public class UnauthorizedStoreAccessException extends BusinessException {

    public UnauthorizedStoreAccessException() {
        super(ErrorCode.OWNER_STORE_ACCESS_DENIED);
    }

    public UnauthorizedStoreAccessException(String message) {
        super(ErrorCode.OWNER_STORE_ACCESS_DENIED, message);
    }

    public UnauthorizedStoreAccessException(Long storeId, Long ownerId) {
        super(ErrorCode.OWNER_STORE_ACCESS_DENIED, 
            String.format("Owner %d is not authorized to access store %d", ownerId, storeId));
    }
}