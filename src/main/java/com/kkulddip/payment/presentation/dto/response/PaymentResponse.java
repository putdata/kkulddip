package com.kkulddip.payment.presentation.dto.response;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;

import java.time.LocalDateTime;

public record PaymentResponse(
    String paymentKey,
    String orderId,
    String orderName,
    long amount,
    String customerName,
    String status,
    String method,
    LocalDateTime requestedAt,
    LocalDateTime approvedAt,
    String receiptUrl
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getPaymentKey() != null ? payment.getPaymentKey().value() : null,
            payment.getOrderId().toString(),
            payment.getOrderName(),
            payment.getAmount().value(),
            payment.getCustomerName(),
            payment.getStatus().name(),
            payment.getMethod() != null ? payment.getMethod().name() : null,
            payment.getRequestedAt(),
            payment.getApprovedAt(),
            payment.getReceiptUrl()
        );
    }

    public static PaymentResponse from(Payment payment, TossPaymentResponse tossResponse) {
        return new PaymentResponse(
            payment.getPaymentKey().value(),
            payment.getOrderId().toString(),
            payment.getOrderName(),
            payment.getAmount().value(),
            payment.getCustomerName(),
            payment.getStatus().name(),
            payment.getMethod().name(),
            payment.getRequestedAt(),
            payment.getApprovedAt(),
            payment.getReceiptUrl()
        );
    }
}