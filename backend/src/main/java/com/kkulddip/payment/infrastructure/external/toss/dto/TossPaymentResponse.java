package com.kkulddip.payment.infrastructure.external.toss.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TossPaymentResponse(
    String paymentKey,
    String orderId,
    String orderName,
    long amount,
    String status,
    String method,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    LocalDateTime requestedAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    LocalDateTime approvedAt,
    Receipt receipt
) {
    public record Receipt(
        String url
    ) {}
    
    public String getReceiptUrl() {
        return receipt != null ? receipt.url() : null;
    }
}