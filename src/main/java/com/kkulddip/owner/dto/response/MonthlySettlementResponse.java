package com.kkulddip.owner.dto.response;

import java.time.YearMonth;
import java.util.List;

public record MonthlySettlementResponse(
    Long storeId,
    String storeName,
    List<MonthlySettlementData> monthlyData,
    YearMonth startPeriod,
    YearMonth endPeriod,
    Integer totalMonths,
    Long totalRevenue,
    Long totalOrderCount,
    Double averageMonthlyRevenue,
    Double overallGrowthRate
) {
    
    public record MonthlySettlementData(
        YearMonth period,
        Long totalRevenue,
        Long orderCount,
        Double avgOrderAmount,
        Long previousMonthRevenue,
        Long previousMonthOrderCount,
        Integer revenueGrowthRate,
        Integer orderCountGrowthRate
    ) {}
}