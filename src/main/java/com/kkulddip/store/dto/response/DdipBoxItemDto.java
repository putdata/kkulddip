package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 띱박스 구성상품 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DdipBoxItemDto {

    /**
     * 구성상품 ID
     */
    private Long itemId;

    /**
     * 구성상품명
     */
    private String ddipboxItemName;

    /**
     * 개별 원가
     */
    private Integer originalPrice;

    /**
     * 수량
     */
    private Integer itemQuantity;
}