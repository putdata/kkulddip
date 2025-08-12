package com.kkulddip.analytics.dto.response;

public record TopSellingItemDto(
    String itemName,
    Long totalQuantity,
    Double percentage
) {}
