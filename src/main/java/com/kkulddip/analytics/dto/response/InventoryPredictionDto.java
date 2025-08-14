package com.kkulddip.analytics.dto.response;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record InventoryPredictionDto(
    LocalDate date,
    Long predictedDailyQuantity,
    Long predictedRemainingQuantity,
    Double inventoryRatio,
    Double confidence
) {
}