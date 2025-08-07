package com.kkulddip.common.event;

import lombok.Builder;

@Builder
public record PaymentResultEvent(
    Long orderId,
    String status
) {}