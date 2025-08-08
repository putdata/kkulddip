package com.kkulddip.favorite.dto.request;

import com.kkulddip.favorite.enums.FavoriteSortType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 즐겨찾기 목록 조회 요청 DTO
 */
public record GetFavoritesRequest(
    @NotNull(message = "소비자 아이디는 필수입니다.")
    Long customerId,
    
    String cursor,
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    Integer size,
    
    FavoriteSortType sortBy
) {

    public GetFavoritesRequest {
        // 기본값 설정
        if (size == null) {
            size = 20;
        }
        if (sortBy == null) {
            sortBy = FavoriteSortType.CREATED_DESC;
        }
    }

    /**
     * 기본값으로 GetFavoritesRequest 생성
     */
    public static GetFavoritesRequest of(Long customerId) {
        return new GetFavoritesRequest(customerId, null, 20, FavoriteSortType.CREATED_DESC);
    }

    /**
     * 커서와 함께 GetFavoritesRequest 생성
     */
    public static GetFavoritesRequest of(Long customerId, String cursor) {
        return new GetFavoritesRequest(customerId, cursor, 20, FavoriteSortType.CREATED_DESC);
    }

    /**
     * 전체 필드로 GetFavoritesRequest 생성
     */
    public static GetFavoritesRequest of(
        Long customerId,
        String cursor,
        Integer size,
        FavoriteSortType sortBy
    ) {
        return new GetFavoritesRequest(customerId, cursor, size, sortBy);
    }
}