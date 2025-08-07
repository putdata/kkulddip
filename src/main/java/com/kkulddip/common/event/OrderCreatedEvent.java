package com.kkulddip.common.event;

import lombok.Builder;

@Builder
public record OrderCreatedEvent(
    Long orderId,
    Integer amount
) {}