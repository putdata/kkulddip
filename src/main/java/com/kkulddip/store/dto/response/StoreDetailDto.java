package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 가게 상세 조회 응답 DTO
 * 개별 가게의 상세 정보를 제공
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StoreDetailDto(
    Long storeId,
    Long ownerId,
    String storeName,
    String storeAddress,
    String description,
    String operatingHours,
    String phoneNumber,
    Double ratingAverage,
    Long reviewCount,
    String businessNumber,
    String storeProfileImage,
    Double latitude,
    Double longitude,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<DdipBoxSummaryDto> ddipBoxes
) {

    /**
     * 띱박스 요약 정보
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DdipBoxSummaryDto(
        Long ddipboxId,
        String ddipboxName,
        String category,
        Long originalPrice,
        Long salePrice,
        Long remainingQuantity,
        Boolean active
    ) {
        
        /**
         * DdipBoxSummaryDto 생성
         */
        public static DdipBoxSummaryDto of(
                Long ddipboxId,
                String ddipboxName,
                String category,
                Long originalPrice,
                Long salePrice,
                Long remainingQuantity,
                Boolean active
        ) {
            return new DdipBoxSummaryDto(ddipboxId, ddipboxName, category, originalPrice, salePrice, remainingQuantity, active);
        }
    }
    
    /**
     * 기본 필드만으로 StoreDetailDto 생성
     */
    public static StoreDetailDto of(Long storeId, String storeName, String storeAddress) {
        return new StoreDetailDto(
                storeId, null, storeName, storeAddress, null, null, null, null, null, null, null, null, null, null, null, null, null
        );
    }
    
    /**
     * 전체 필드로 StoreDetailDto 생성
     */
    public static StoreDetailDto of(
            Long storeId,
            Long ownerId,
            String storeName,
            String storeAddress,
            String description,
            String operatingHours,
            String phoneNumber,
            Double ratingAverage,
            Long reviewCount,
            String businessNumber,
            String storeProfileImage,
            Double latitude,
            Double longitude,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<DdipBoxSummaryDto> ddipBoxes
    ) {
        return new StoreDetailDto(
                storeId, ownerId, storeName, storeAddress, description, operatingHours, phoneNumber,
                ratingAverage, reviewCount, businessNumber, storeProfileImage, latitude, longitude,
                active, createdAt, updatedAt, ddipBoxes
        );
    }
}