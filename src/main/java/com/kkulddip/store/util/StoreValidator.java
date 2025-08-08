package com.kkulddip.store.util;

import com.kkulddip.store.exception.StoreValidationException;

/**
 * Store 도메인 관련 유효성 검증 유틸리티 클래스
 */
public final class StoreValidator {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int MIN_PAGE_SIZE = 1;

    private StoreValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * 가게 ID 유효성 검증
     *
     * @param storeId 검증할 가게 ID
     * @throws StoreValidationException 유효하지 않은 ID인 경우
     */
    public static void validateStoreId(Long storeId) {
        if (storeId == null || storeId <= 0) {
            throw StoreValidationException.invalidStoreId(storeId);
        }
    }

    /**
     * 페이지 크기 유효성 검증
     *
     * @param size 검증할 페이지 크기
     * @throws StoreValidationException 유효하지 않은 크기인 경우
     */
    public static void validatePageSize(Integer size) {
        if (size == null || size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            throw StoreValidationException.invalidPageSize(size);
        }
    }

    /**
     * 검색 키워드 유효성 검증
     *
     * @param keyword 검증할 키워드
     * @throws StoreValidationException 유효하지 않은 키워드인 경우
     */
    public static void validateSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw StoreValidationException.emptySearchKeyword();
        }
    }

    /**
     * 카테고리 유효성 검증
     *
     * @param category 검증할 카테고리
     * @throws StoreValidationException 유효하지 않은 카테고리인 경우
     */
    public static void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw StoreValidationException.emptyCategory();
        }
    }

    /**
     * 정렬 타입 유효성 검증
     *
     * @param sortBy 검증할 정렬 타입
     * @return 검증된 정렬 타입 (소문자)
     * @throws StoreValidationException 지원하지 않는 정렬 타입인 경우
     */
    public static String validateAndNormalizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "id"; // 기본값
        }

        String normalizedSortBy = sortBy.toLowerCase().trim();

        switch (normalizedSortBy) {
            case "id":
            case "created_at":
            case "createdat":
            case "rating":
            case "distance":
                return normalizedSortBy;
            default:
                throw StoreValidationException.invalidSortType(sortBy);
        }
    }
}