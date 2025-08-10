package com.kkulddip.storeManagement.dto.response;

import lombok.Builder;

/**
 * 띱박스 아이템 관리 응답 DTO
 */
@Builder
public record DdipBoxItemManagementResponse(
    
    Long itemId,
    Long ddipboxId,
    String ddipboxItemName,
    Integer originalPrice,
    Integer itemQuantity
    
) {
    
    public static DdipBoxItemManagementResponse from(com.kkulddip.store.entity.DdipBoxItem ddipBoxItem) {
        return DdipBoxItemManagementResponse.builder()
            .itemId(ddipBoxItem.getItemId())
            .ddipboxId(ddipBoxItem.getDdipBox().getDdipboxId())
            .ddipboxItemName(ddipBoxItem.getDdipboxItemName())
            .originalPrice(ddipBoxItem.getOriginalPrice())
            .itemQuantity(ddipBoxItem.getItemQuantity())
            .build();
    }
}