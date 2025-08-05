package com.kkulddip.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가게 목록 조회 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreListRequest {

    /**
     * 사용자 위도 (거리 계산용)
     */
    private Double userLatitude;

    /**
     * 사용자 경도 (거리 계산용)
     */
    private Double userLongitude;

    /**
     * 정렬 기준 (id, created_at, rating, distance)
     */
    @Builder.Default
    private String sortBy = "id";

    /**
     * 페이지 크기 (최대 50)
     */
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
    @Builder.Default
    private Integer size = 10;

    /**
     * 커서 (페이지네이션용)
     */
    private String cursor;
}