package com.kkulddip.favorite.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 즐겨찾기를 찾을 수 없을 때 발생하는 예외
 */
public class FavoriteNotFoundException extends BusinessException {

    public FavoriteNotFoundException() {
        super(ErrorCode.FAVORITE_NOT_FOUND);
    }

    public FavoriteNotFoundException(Long favoriteId) {
        super(ErrorCode.FAVORITE_NOT_FOUND, "favoriteId: " + favoriteId);
    }
}