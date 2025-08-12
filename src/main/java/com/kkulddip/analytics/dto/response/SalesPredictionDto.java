package com.kkulddip.analytics.dto.response;

import java.time.LocalDate;

public record SalesPredictionDto(
    LocalDate date,
    Double predictedRevenue,
    Double confidence
) {}
