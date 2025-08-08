package com.kkulddip.favorite.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 즐겨찾기가 이미 존재할 때 발생하는 예외
 */
public class FavoriteAlreadyExistsException extends BusinessException {

    public FavoriteAlreadyExistsException() {
        super(ErrorCode.FAVORITE_ALREADY_EXISTS);
    }

    public FavoriteAlreadyExistsException(Long customerId, Long storeId) {
        super(ErrorCode.FAVORITE_ALREADY_EXISTS, 
            "customerId: " + customerId + ", storeId: " + storeId);
    }
}