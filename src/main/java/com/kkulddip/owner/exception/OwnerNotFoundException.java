package com.kkulddip.owner.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

public class OwnerNotFoundException extends BusinessException {

    public OwnerNotFoundException() {
        super(ErrorCode.OWNER_NOT_FOUND);
    }

    public OwnerNotFoundException(String message) {
        super(ErrorCode.OWNER_NOT_FOUND, message);
    }

    public OwnerNotFoundException(Long ownerId) {
        super(ErrorCode.OWNER_NOT_FOUND, "Owner not found with ID: " + ownerId);
    }
}