package com.kkulddip.storeManagement.dto.response;

import lombok.Builder;

/**
 * 띱박스 관리 응답 DTO
 */
@Builder
public record DdipBoxManagementResponse(
    
    Long ddipboxId,
    Long storeId,
    String ddipboxName,
    String description,
    String category,
    Long originalPrice,
    Long salePrice,
    Long dailyQuantity,
    Long remainingQuantity,
    Long maxPerCustomer,
    Boolean isActive
    
) {
    
    public static DdipBoxManagementResponse from(com.kkulddip.store.entity.DdipBox ddipBox) {
        return DdipBoxManagementResponse.builder()
            .ddipboxId(ddipBox.getDdipboxId())
            .storeId(ddipBox.getStore().getStoreId())
            .ddipboxName(ddipBox.getDdipboxName())
            .description(ddipBox.getDescription())
            .category(ddipBox.getCategory())
            .originalPrice(ddipBox.getOriginalPrice())
            .salePrice(ddipBox.getSalePrice())
            .dailyQuantity(ddipBox.getDailyQuantity())
            .remainingQuantity(ddipBox.getRemainingQuantity())
            .maxPerCustomer(ddipBox.getMaxPerCustomer())
            .isActive(ddipBox.getIsActive())
            .build();
    }
}