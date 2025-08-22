package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CreateOrderResponse(
    String orderId,      // Long -> String으로 변경
    Long customerId,
    Long storeId,
    Long originalPrice,
    Long finalPrice,
    String orderStatus,
    LocalDateTime orderDate
) {}