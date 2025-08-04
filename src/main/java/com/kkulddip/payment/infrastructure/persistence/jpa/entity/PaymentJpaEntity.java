package com.kkulddip.payment.infrastructure.persistence.jpa.entity;

import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_key", unique = true)
    private String paymentKey;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "callback_url")
    private String callbackUrl;

    @Column(name = "fail_url")
    private String failUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    private PaymentMethod method;

    @Column(name = "requested_at")
    private String requestedAt;

    @Column(name = "approved_at")
    private String approvedAt;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PaymentJpaEntity(String paymentKey, String orderId, String orderName,
                            long amount, String customerName, String customerEmail,
                            String callbackUrl, String failUrl, PaymentStatus status,
                            PaymentMethod method, String requestedAt, String approvedAt,
                            String receiptUrl) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.callbackUrl = callbackUrl;
        this.failUrl = failUrl;
        this.status = status;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
    }

    public static PaymentJpaEntity from(Payment payment) {
        return new PaymentJpaEntity(
                payment.getPaymentKey() != null ? payment.getPaymentKey().value() : null,
                payment.getOrderId(),
                payment.getOrderName(),
                payment.getAmount().value(),
                payment.getCustomerName(),
                payment.getCustomerEmail(),
                payment.getCallbackUrl(),
                payment.getFailUrl(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getRequestedAt(),
                payment.getApprovedAt(),
                payment.getReceiptUrl()
        );
    }

    public Payment toDomain() {
        return new Payment(
                this.paymentKey != null ? PaymentKey.of(this.paymentKey) : null,
                this.orderId,
                this.orderName,
                Money.of(this.amount),
                this.customerName,
                this.customerEmail,
                this.status,
                this.method,
                this.requestedAt,
                this.approvedAt,
                this.receiptUrl,
                this.callbackUrl,
                this.failUrl,
                this.createdAt,
                this.updatedAt
        );
    }

    public void updateFrom(Payment payment) {
        this.paymentKey = payment.getPaymentKey() != null ? payment.getPaymentKey().value() : null;
        this.orderName = payment.getOrderName();
        this.amount = payment.getAmount().value();
        this.customerName = payment.getCustomerName();
        this.status = payment.getStatus();
        this.method = payment.getMethod();
        this.requestedAt = payment.getRequestedAt();
        this.approvedAt = payment.getApprovedAt();
        this.receiptUrl = payment.getReceiptUrl();
    }
}