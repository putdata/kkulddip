package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * 가게 리스트 조회 응답 DTO
 * 가게 목록 페이지에서 사용되는 카드뷰 형태의 데이터
 */
@Builder
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
) {}