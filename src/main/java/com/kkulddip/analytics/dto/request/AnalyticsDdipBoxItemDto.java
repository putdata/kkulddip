package com.kkulddip.analytics.dto.request;

public record AnalyticsDdipBoxItemDto(
    Long itemId,
    String ddipboxItemName,
    Integer originalPrice,
    Integer itemQuantity,
    Integer weight
) { }
