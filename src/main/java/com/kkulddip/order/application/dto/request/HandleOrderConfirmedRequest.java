package com.kkulddip.order.application.dto.request;

import lombok.Builder;

@Builder
public record HandleOrderConfirmedRequest(
    Long orderId,
    Long storeId
) {}