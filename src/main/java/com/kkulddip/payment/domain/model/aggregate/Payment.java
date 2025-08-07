package com.kkulddip.payment.domain.model.aggregate;

import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    private PaymentKey paymentKey;
    private Long orderId;
    private String orderName;
    private Money amount;
    private String customerName;
    private String customerEmail;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String receiptUrl;
    private String callbackUrl;
    private String failUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(Long orderId, String orderName, Money amount, String customerName, String customerEmail, String callbackUrl, String failUrl) {
        this.orderId = orderId;
        this.orderName = validateOrderName(orderName);
        this.amount = amount;
        this.customerName = validateCustomerName(customerName);
        this.customerEmail = customerEmail;
        this.callbackUrl = callbackUrl;
        this.failUrl = failUrl;
        this.status = PaymentStatus.READY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 재구성을 위한 생성자
    public Payment(PaymentKey paymentKey, Long orderId, String orderName, Money amount,
        String customerName, String customerEmail, PaymentStatus status, PaymentMethod method,
        LocalDateTime requestedAt, LocalDateTime approvedAt, String receiptUrl, String callbackUrl, String failUrl,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
        this.callbackUrl = callbackUrl;
        this.failUrl = failUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void approve(PaymentKey paymentKey, PaymentMethod method,
        LocalDateTime requestedAt, LocalDateTime approvedAt, String receiptUrl) {
            
        if (this.status != PaymentStatus.READY) {
            throw new IllegalStateException("준비 상태의 결제만 승인할 수 있습니다.");
        }

        this.paymentKey = paymentKey;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
        this.status = PaymentStatus.DONE;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (!this.status.canCancel()) {
            throw new IllegalStateException("취소할 수 없는 결제 상태입니다.");
        }

        this.status = PaymentStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = PaymentStatus.ABORTED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isApproved() {
        return this.status == PaymentStatus.DONE;
    }

    private String validateOrderName(String orderName) {
        if (orderName == null || orderName.trim().isEmpty()) {
            throw new IllegalArgumentException("주문명은 필수입니다.");
        }
        return orderName;
    }

    private String validateCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("고객명은 필수입니다.");
        }
        return customerName;
    }
}