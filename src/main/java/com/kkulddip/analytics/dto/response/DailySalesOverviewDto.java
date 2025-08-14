package com.kkulddip.analytics.dto.response;

public record DailySalesOverviewDto(
    Long totalSales,
    Integer totalOrderCount,
    Long averageOrderAmount
) { }