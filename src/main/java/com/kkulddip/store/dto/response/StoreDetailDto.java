package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 가게 상세 조회 응답 DTO
 * 개별 가게의 상세 정보를 제공
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreDetailDto {

    /**
     * 가게 ID
     */
    private Long storeId;

    /**
     * 사업자 ID
     */
    private Long ownerId;

    /**
     * 가게명
     */
    private String storeName;

    /**
     * 가게 주소
     */
    private String storeAddress;

    /**
     * 가게 설명
     */
    private String description;

    /**
     * 운영시간
     */
    private String operatingHours;

    /**
     * 전화번호
     */
    private String phoneNumber;

    /**
     * 평균 평점
     */
    private Double ratingAverage;

    /**
     * 리뷰 수
     */
    private Long reviewCount;

    /**
     * 사업자 등록번호
     */
    private String businessNumber;

    /**
     * 가게 프로필 이미지
     */
    private String storeProfileImage;

    /**
     * 위도
     */
    private Double latitude;

    /**
     * 경도
     */
    private Double longitude;

    /**
     * 가게 활성화 상태
     */
    private Boolean isActive;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * 띱박스 목록 (간단한 정보만)
     */
    private List<DdipBoxSummaryDto> ddipBoxes;

    /**
     * 띱박스 요약 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DdipBoxSummaryDto {
        private Long ddipboxId;
        private String ddipboxName;
        private String category;
        private Long originalPrice;
        private Long salePrice;
        private Long remainingQuantity;
        private Boolean isActive;
    }
}