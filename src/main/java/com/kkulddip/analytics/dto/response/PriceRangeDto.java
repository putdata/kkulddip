package com.kkulddip.analytics.dto.response;

public record PriceRangeDto(
    String priceRange,
    Long count,
    Double percentage
) {}
