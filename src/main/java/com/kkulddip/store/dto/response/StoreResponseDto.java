package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 가게 리스트 조회 응답 DTO
 * 가게 목록 페이지에서 사용되는 카드뷰 형태의 데이터
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StoreResponseDto(
    Long storeId,
    Long ownerId,
    String storeName,
    String storeAddress,
    String description,
    String operatingHours,
    String phoneNumber,
    Double ratingAverage,
    Long reviewNum,
    Double distanceFromUser,
    String representativeDdipboxName,
    Long representativeOriginalPrice,
    Long representativeSalePrice,
    String storeProfileImage,
    Boolean active
) {
    
    /**
     * 기본 필드만으로 StoreResponseDto 생성
     */
    public static StoreResponseDto of(Long storeId, String storeName, String storeAddress) {
        return new StoreResponseDto(
                storeId, null, storeName, storeAddress, null, null, null, null, null, null, null, null, null, null, null
        );
    }
    
    /**
     * 전체 필드로 StoreResponseDto 생성
     */
    public static StoreResponseDto of(
            Long storeId,
            Long ownerId,
            String storeName,
            String storeAddress,
            String description,
            String operatingHours,
            String phoneNumber,
            Double ratingAverage,
            Long reviewNum,
            Double distanceFromUser,
            String representativeDdipboxName,
            Long representativeOriginalPrice,
            Long representativeSalePrice,
            String storeProfileImage,
            Boolean active
    ) {
        return new StoreResponseDto(
                storeId, ownerId, storeName, storeAddress, description, operatingHours, phoneNumber,
                ratingAverage, reviewNum, distanceFromUser, representativeDdipboxName,
                representativeOriginalPrice, representativeSalePrice, storeProfileImage, active
        );
    }
}