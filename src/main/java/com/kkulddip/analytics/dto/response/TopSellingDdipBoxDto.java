package com.kkulddip.analytics.dto.response;

public record TopSellingDdipBoxDto(
    Long ddipBoxId,
    String ddipBoxName,
    Integer quantitySold,
    Long totalSalesAmount
) { }