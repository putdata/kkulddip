package com.kkulddip.order.infrastructure.integration.dto.request;

import lombok.Builder;

@Builder
public record PaymentRequest(
    Long orderId,
    Long customerId,
    Integer amount
) {}