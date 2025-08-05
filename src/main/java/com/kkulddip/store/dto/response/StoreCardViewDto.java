package com.kkulddip.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 가게 리스트의 카드뷰 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCardViewDto {
    private Long storeId;
    private String storeName;
    private String storeProfileImage;
    private String representativeDdipboxName;
    private Long representativeOriginalPrice;
    private Long representativeSalePrice;
    private Double reviewRating;
    private Double distanceFromUser; // 사용자와의 거리 (km)
}