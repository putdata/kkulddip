package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Store Detail page 응답용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreDetailPageResponse {

    private Long storeId;
    private Long ownerId;
    private String storeName;
    private String storeAddress;
    private String description;
    private String operatingHours;
    private String phoneNumber;
    private Double ratingAverage;
    private Long reviewNum; // reviewCount 대신 reviewNum 사용

    // 추가 필드들 (선택적으로 포함)
    private String storeProfileImage;
    private String businessNumber;
    private Boolean isActive;
    private Double latitude;
    private Double longitude;

    // 거리 정보 (거리순 정렬시에만 포함)
    private Double distanceFromUser;

    // 대표 띱박스 정보 (선택적)
    private String representativeDdipboxName;
    private Long representativeOriginalPrice;
    private Long representativeSalePrice;
}