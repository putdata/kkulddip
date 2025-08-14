package com.kkulddip.analytics.dto.request;

import java.util.List;

public record AnalyticsOrderItemDataDto(
    Long orderItemId,
    Long productId,
    String productName,
    Integer quantity,
    Long unitPrice,
    Long totalPrice, // count * unit_price
    Long unitCostPrice, // 원가 (개당)
    Long totalCostPrice, // 총 원가 (count * unit_cost_price)
    List<AnalyticsDdipBoxItemDto> ddipBoxItems
) { }
