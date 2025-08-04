package com.kkulddip.payment.interfaces.dto.response;

import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;

public record PaymentResponse(
    String paymentKey,
    String orderId,
    String orderName,
    long amount,
    String customerName,
    String status,
    String method,
    String requestedAt,
    String approvedAt,
    String receiptUrl
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentKey() != null ? payment.getPaymentKey().value() : null,
                payment.getOrderId(),
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
                payment.getOrderId(),
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