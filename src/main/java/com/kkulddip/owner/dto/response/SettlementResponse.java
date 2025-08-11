package com.kkulddip.owner.dto.response;

import java.time.YearMonth;

public record SettlementResponse(
    Long storeId,
    String storeName,
    YearMonth period,
    Long totalRevenue,
    Long orderCount,
    Long avgOrderAmount,
    Long previousMonthRevenue,
    Long revenueGrowthRate,
    Long previousMonthOrderCount,
    Long orderCountGrowthRate
) {}