package com.kkulddip.order.infrastructure.integration.dto.response;

import lombok.Builder;

@Builder
public record PaymentResponse(
    String paymentKey,
    String paymentUrl,
    String status,
    Long orderId,
    Integer amount
) {}