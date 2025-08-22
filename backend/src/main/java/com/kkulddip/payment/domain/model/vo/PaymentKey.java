package com.kkulddip.payment.domain.model.vo;

/**
 * 결제키를 나타내는 값 객체
 */
public record PaymentKey(
    String value
) {
    
    public PaymentKey {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("결제키는 필수입니다.");
        }
    }
    
    public static PaymentKey of(String value) {
        return new PaymentKey(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}