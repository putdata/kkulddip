package com.kkulddip.analytics.dto.response;

public record DailyInventoryStatusDto(
    Long totalDailyCount,
    Long totalRemainingCount,
    Double remainingPercentage
) { }