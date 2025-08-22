package com.kkulddip.analytics.dto.request;

import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Builder
public record AnalyticsRequestDto(
    Long storeId,
    LocalDate startDate,
    LocalDate endDate,
    List<AnalyticsOrderDataDto> orders,
    List<DailyInventoryDataDto> dailyInventoryData
) { }
