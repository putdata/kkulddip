package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * 띱박스 구성상품 DTO
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DdipBoxItemDto(
    Long itemId,
    String ddipboxItemName,
    Integer originalPrice,
    Integer itemQuantity
) {}