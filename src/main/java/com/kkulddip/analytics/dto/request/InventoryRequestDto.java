package com.kkulddip.analytics.dto.request;

import java.time.LocalDate;
import java.util.List;

public record InventoryRequestDto(
    Long storeId,
    LocalDate startDate,
    LocalDate endDate,
    List<DailyInventoryDataDto> dailyInventoryData
) {
}