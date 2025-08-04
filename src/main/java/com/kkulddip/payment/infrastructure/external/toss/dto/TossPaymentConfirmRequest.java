package com.kkulddip.payment.infrastructure.external.toss.dto;

public record TossPaymentConfirmRequest(
    String paymentKey,
    String orderId,
    long amount
) {}