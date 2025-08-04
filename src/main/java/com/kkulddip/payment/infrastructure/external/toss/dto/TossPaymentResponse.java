package com.kkulddip.payment.infrastructure.external.toss.dto;

public record TossPaymentResponse(
    String paymentKey,
    String orderId,
    String orderName,
    long amount,
    String status,
    String method,
    String requestedAt,
    String approvedAt,
    Receipt receipt
) {
    public record Receipt(
        String url
    ) {}
    
    public String getReceiptUrl() {
        return receipt != null ? receipt.url() : null;
    }
}