package com.kkulddip.analytics.dto.response;

public record HighInventoryDdipBoxDto(
    Long ddipBoxId,
    String ddipBoxName,
    Long remainingCount,
    Long dailyCount
) { }