package com.kkulddip.order.application.dto.request;

import lombok.Builder;

@Builder
public record HandlePaymentResultRequest(
    Long orderId,
    String status
) {}