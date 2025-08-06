package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 띱박스 구성상품 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DdipBoxItemDto(
    Long itemId,
    String ddipboxItemName,
    Integer originalPrice,
    Integer itemQuantity
) {
    
    /**
     * DdipBoxItemDto 생성
     */
    public static DdipBoxItemDto of(
            Long itemId,
            String ddipboxItemName,
            Integer originalPrice,
            Integer itemQuantity
    ) {
        return new DdipBoxItemDto(itemId, ddipboxItemName, originalPrice, itemQuantity);
    }
}