package com.kkulddip.payment.presentation.dto.request;

/**
 * 결제 요청 DTO
 */
public record RequestPaymentRequest(
    String orderId  // Long -> String으로 변경
) {}