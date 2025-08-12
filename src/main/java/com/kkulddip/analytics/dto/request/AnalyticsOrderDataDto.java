package com.kkulddip.analytics.dto.request;

import java.time.LocalDate;
import java.util.List;

public record AnalyticsOrderDataDto(
    Long orderId,
    LocalDate orderDate,
    Long totalAmount,
    List<AnalyticsOrderItemDataDto> orderItems
) { }
