package com.kkulddip.store.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 가게 관련 유효성 검증 실패시 발생하는 예외
 */
public class StoreValidationException extends BusinessException {

    public StoreValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StoreValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 유효하지 않은 가게 ID
     */
    public static StoreValidationException invalidStoreId(Long storeId) {
        return new StoreValidationException(ErrorCode.STORE_INVALID_ID, "가게 ID: " + storeId);
    }

    /**
     * 유효하지 않은 페이지 크기
     */
    public static StoreValidationException invalidPageSize(Integer size) {
        return new StoreValidationException(ErrorCode.STORE_INVALID_PAGE_SIZE, "페이지 크기: " + size);
    }

    /**
     * 검색 키워드 누락
     */
    public static StoreValidationException emptySearchKeyword() {
        return new StoreValidationException(ErrorCode.STORE_SEARCH_KEYWORD_EMPTY);
    }

    /**
     * 카테고리 누락
     */
    public static StoreValidationException emptyCategory() {
        return new StoreValidationException(ErrorCode.STORE_CATEGORY_EMPTY);
    }

    /**
     * 지원하지 않는 정렬 타입
     */
    public static StoreValidationException invalidSortType(String sortType) {
        return new StoreValidationException(ErrorCode.STORE_INVALID_SORT_TYPE, "정렬 타입: " + sortType);
    }
}