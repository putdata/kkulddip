package com.kkulddip.analytics.dto.request;

import java.util.List;

public record AnalyticsOrderItemDataDto(
    Long orderItemId,
    Long productId,
    String productName,
    Integer quantity,
    Long unitPrice,
    Long totalPrice, // count * unit_price
    List<AnalyticsDdipBoxItemDto> ddipBoxItems
) { }
