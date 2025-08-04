package com.kkulddip.payment.interfaces.dto.request;

public record OrderRequestDto(
    String orderId,
    String orderName,
    long amount,
    String customerName,
    String customerEmail,
    String callbackUrl,
    String failUrl,
    Long timestamp
) {}