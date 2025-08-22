package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record OrderPickupResponse(
    String orderId,
    String orderStatus,
    LocalDateTime pickedUpAt
) {}