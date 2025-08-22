package com.kkulddip.payment.domain.model.vo;

import java.util.UUID;

/**
 * 결제 주문 ID를 나타내는 값 객체
 * 클라이언트에게 제공되는 결제 전용 주문 ID (orderId-UUID앞8글자 형태)
 */
public record PaymentOrderId(
    String value
) {
    
    public PaymentOrderId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("결제 주문 ID는 필수입니다.");
        }
    }
    
    public static PaymentOrderId of(String value) {
        return new PaymentOrderId(value);
    }
    
    /**
     * 주문 ID와 UUID를 조합하여 결제 전용 주문 ID를 생성
     * @param orderId 원본 주문 ID
     * @return 생성된 PaymentOrderId (orderId-UUID앞8글자)
     */
    public static PaymentOrderId generate(Long orderId) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return new PaymentOrderId(orderId + "-" + uuid);
    }
}