package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record OrderConfirmationResponse(
    String orderId,
    String orderStatus,
    LocalDateTime pickupTime,
    LocalDateTime confirmedAt
) {}