package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CreateOrderResponse(
    Long orderId,
    Long customerId,
    Long storeId,
    Integer originalPrice,
    Integer finalPrice,
    String orderStatus,
    LocalDateTime orderDate
) {}