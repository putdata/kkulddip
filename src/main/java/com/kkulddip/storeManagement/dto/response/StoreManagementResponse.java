package com.kkulddip.storeManagement.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 가게 관리 응답 DTO
 */
@Builder
public record StoreManagementResponse(
    
    Long storeId,
    Long ownerId,
    String storeName,
    String phone,
    String description,
    String operatingHours,
    Boolean isActive,
    Double ratingAverage,
    Long reviewCount,
    String businessNumber,
    String storeAddress,
    String storeProfileImage,
    Double latitude,
    Double longitude,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
    
) {
    
    public static StoreManagementResponse from(com.kkulddip.store.entity.Store store) {
        return StoreManagementResponse.builder()
            .storeId(store.getStoreId())
            .ownerId(store.getOwnerId())
            .storeName(store.getStoreName())
            .phone(store.getPhone())
            .description(store.getDescription())
            .operatingHours(store.getOperatingHours())
            .isActive(store.getIsActive())
            .ratingAverage(store.getRatingAverage())
            .reviewCount(store.getReviewCount())
            .businessNumber(store.getBusinessNumber())
            .storeAddress(store.getStoreAddress())
            .storeProfileImage(store.getStoreProfileImage())
            .latitude(store.getLatitude())
            .longitude(store.getLongitude())
            .createdAt(store.getCreatedAt())
            .updatedAt(store.getUpdatedAt())
            .build();
    }
}