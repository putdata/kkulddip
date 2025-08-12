package com.kkulddip.owner.dto.response;

import java.time.YearMonth;
import java.util.List;

public record SettlementSummaryResponse(
    YearMonth period,
    Long totalRevenue,
    Long totalOrderCount,
    Long avgOrderAmount,
    Integer storeCount,
    List<SettlementResponse> storeSettlements
) {}