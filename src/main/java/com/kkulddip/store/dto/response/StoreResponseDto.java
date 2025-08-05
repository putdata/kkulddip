package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 가게 리스트 조회 응답 DTO
 * 가게 목록 페이지에서 사용되는 카드뷰 형태의 데이터
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreResponseDto {

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
    private Long reviewNum;

    /**
     * 사용자로부터의 거리 (km)
     * 거리순 정렬시에만 포함
     */
    private Double distanceFromUser;

    /**
     * 대표 띱박스명
     */
    private String representativeDdipboxName;

    /**
     * 대표 띱박스 원가
     */
    private Long representativeOriginalPrice;

    /**
     * 대표 띱박스 할인가
     */
    private Long representativeSalePrice;

    /**
     * 가게 프로필 이미지
     */
    private String storeProfileImage;

    /**
     * 가게 활성화 상태
     */
    private Boolean isActive;
}