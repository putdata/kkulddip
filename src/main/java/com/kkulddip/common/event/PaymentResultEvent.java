package com.kkulddip.common.event;

public record PaymentResultEvent(
    String orderId,
    String status
) {}