package com.kkulddip.payment.infrastructure.persistence.jpa.entity;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.vo.PaymentOrderId;
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
    private Long orderId;

    @Column(name = "payment_order_id", unique = true, nullable = false)
    private String paymentOrderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    private PaymentMethod method;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "payment_order_id_created_at")
    private LocalDateTime paymentOrderIdCreatedAt;

    public PaymentJpaEntity(
        String paymentKey, Long orderId, String paymentOrderId, String orderName,
        long amount, Long customerId, PaymentStatus status, PaymentMethod method, 
        LocalDateTime requestedAt, LocalDateTime approvedAt, LocalDateTime paymentOrderIdCreatedAt
    ) {

        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.paymentOrderId = paymentOrderId;
        this.orderName = orderName;
        this.amount = amount;
        this.customerId = customerId;
        this.status = status;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.paymentOrderIdCreatedAt = paymentOrderIdCreatedAt;
    }

    public static PaymentJpaEntity from(Payment payment) {
        return new PaymentJpaEntity(
            payment.getPaymentKey() != null ? payment.getPaymentKey().value() : null,
            payment.getOrderId(),
            payment.getPaymentOrderId().value(),
            payment.getOrderName(),
            payment.getAmount().value(),
            payment.getCustomerId(),
            payment.getStatus(),
            payment.getMethod(),
            payment.getRequestedAt(),
            payment.getApprovedAt(),
            payment.getPaymentOrderIdCreatedAt()
        );
    }

    public Payment toDomain() {
        return new Payment(
            this.paymentKey != null ? PaymentKey.of(this.paymentKey) : null,
            this.orderId,
            PaymentOrderId.of(this.paymentOrderId),
            this.orderName,
            Money.of(this.amount),
            this.customerId,
            this.status,
            this.method,
            this.requestedAt,
            this.approvedAt,
            this.createdAt,
            this.updatedAt,
            this.paymentOrderIdCreatedAt
        );
    }

    public void updateFrom(Payment payment) {
        this.paymentKey = payment.getPaymentKey() != null ? payment.getPaymentKey().value() : null;
        this.paymentOrderId = payment.getPaymentOrderId().value();
        this.orderName = payment.getOrderName();
        this.amount = payment.getAmount().value();
        this.customerId = payment.getCustomerId();
        this.status = payment.getStatus();
        this.method = payment.getMethod();
        this.requestedAt = payment.getRequestedAt();
        this.approvedAt = payment.getApprovedAt();
        this.paymentOrderIdCreatedAt = payment.getPaymentOrderIdCreatedAt();
    }
}