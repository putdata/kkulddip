package com.kkulddip.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 가게 검색 요청 DTO
 */
public record StoreSearchRequest(
    @NotBlank(message = "검색 키워드는 필수입니다.")
    String keyword,
    Double userLatitude,
    Double userLongitude,
    String sortBy,
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
    Integer size,
    String cursor
) {
    
    public StoreSearchRequest {
        // 기본값 설정
        if (sortBy == null) {
            sortBy = "id";
        }
        // size는 null이면 기본값 10을 사용하지만, 명시적으로 null이 전달된 경우는 예외 발생
        // 따라서 기본값 설정 제거
    }
    
    /**
     * 기본 값으로 StoreSearchRequest 생성
     */
    public static StoreSearchRequest of(String keyword) {
        return new StoreSearchRequest(keyword, null, null, "id", 10, null);
    }
    
    /**
     * 사용자 위치와 함껴 StoreSearchRequest 생성
     */
    public static StoreSearchRequest of(String keyword, Double userLatitude, Double userLongitude) {
        return new StoreSearchRequest(keyword, userLatitude, userLongitude, "id", 10, null);
    }
    
    /**
     * 전체 필드로 StoreSearchRequest 생성
     */
    public static StoreSearchRequest of(
            String keyword,
            Double userLatitude,
            Double userLongitude,
            String sortBy,
            Integer size,
            String cursor
    ) {
        return new StoreSearchRequest(keyword, userLatitude, userLongitude, sortBy, size, cursor);
    }
}