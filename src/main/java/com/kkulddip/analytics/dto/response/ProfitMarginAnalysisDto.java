package com.kkulddip.analytics.dto.response;

import java.util.List;

public record ProfitMarginAnalysisDto(
    Long totalRevenue,
    Long totalCost,
    Long totalProfit,
    Double profitMarginPercentage,
    List<ProfitMarginByRangeDto> profitByMarginRanges
) { }