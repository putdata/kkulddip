package com.kkulddip.analytics.dto.response;

public record ProfitMarginByRangeDto(
    String marginRange,
    Long salesAmount,
    Integer productCount
) { }