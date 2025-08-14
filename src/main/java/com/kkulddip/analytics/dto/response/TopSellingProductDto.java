package com.kkulddip.analytics.dto.response;

import lombok.Builder;

@Builder
public record TopSellingProductDto(
    String productName,
    Integer totalQuantity,
    Double percentage
) {
}