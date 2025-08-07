package com.kkulddip.common.event;

import lombok.Builder;

@Builder
public record OrderConfirmedEvent(
    Long orderId,
    Long storeId
) {}