package com.kkulddip.analytics.dto.request;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record DailyInventoryDataDto(
    LocalDate date,
    Long totalDailyQuantity,
    Long totalRemainingQuantity
) {
}