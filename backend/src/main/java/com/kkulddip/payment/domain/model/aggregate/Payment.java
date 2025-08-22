package com.kkulddip.payment.domain.model.aggregate;

import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.vo.PaymentOrderId;
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
    private PaymentOrderId paymentOrderId;
    private String orderName;
    private Money amount;
    private Long customerId;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paymentOrderIdCreatedAt; // PaymentOrderId TTL을 위한 생성 시간

    public Payment(Long orderId, String orderName, Money amount, Long customerId) {
        this.orderId = orderId;
        this.paymentOrderId = PaymentOrderId.generate(orderId);
        this.orderName = validateOrderName(orderName);
        this.amount = amount;
        this.customerId = customerId;
        this.status = PaymentStatus.READY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paymentOrderIdCreatedAt = LocalDateTime.now(); // PaymentOrderId 생성 시간 기록
    }

    // 재구성을 위한 생성자
    public Payment(PaymentKey paymentKey, Long orderId, PaymentOrderId paymentOrderId, String orderName, Money amount,
        Long customerId, PaymentStatus status, PaymentMethod method,
        LocalDateTime requestedAt, LocalDateTime approvedAt,
        LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime paymentOrderIdCreatedAt) {

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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paymentOrderIdCreatedAt = paymentOrderIdCreatedAt;
    }

    public void approve(PaymentKey paymentKey, PaymentMethod method,
        LocalDateTime requestedAt, LocalDateTime approvedAt) {
            
        if (this.status != PaymentStatus.READY) {
            throw new IllegalStateException("준비 상태의 결제만 승인할 수 있습니다.");
        }

        this.paymentKey = paymentKey;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
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

    /**
     * PaymentOrderId TTL 검증 (5분)
     */
    public boolean isPaymentOrderIdExpired() {
        if (paymentOrderIdCreatedAt == null) {
            return true;
        }
        return paymentOrderIdCreatedAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }

    /**
     * 새로운 PaymentOrderId 생성 및 TTL 갱신
     */
    public void regeneratePaymentOrderId() {
        this.paymentOrderId = PaymentOrderId.generate(this.orderId);
        this.paymentOrderIdCreatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 현재 PaymentOrderId 반환 (TTL 검증 포함)
     */
    public String getValidPaymentOrderId() {
        if (isPaymentOrderIdExpired()) {
            regeneratePaymentOrderId();
        }
        return this.paymentOrderId.value();
    }
    
    /**
     * 결제 재시도를 위한 초기화 (새로운 PaymentOrderId 생성 + READY 상태로 변경)
     */
    public void regenerateForRetry() {
        this.paymentOrderId = PaymentOrderId.generate(this.orderId);
        this.paymentOrderIdCreatedAt = LocalDateTime.now();
        this.status = PaymentStatus.READY;
        this.paymentKey = null; // 이전 결제 키 제거
        this.method = null;
        this.requestedAt = null;
        this.approvedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
}