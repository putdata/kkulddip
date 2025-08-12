package com.kkulddip.analytics.dto.response;

import java.util.List;

public record AnalyticsResponseDto(
    List<TopSellingItemDto> topSellingItems,
    List<PriceRangeDto> topPriceRanges,
    List<SalesPredictionDto> salesPrediction,
    Double totalRevenue,
    Integer totalOrders
) {}
