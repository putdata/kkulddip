package com.kkulddip.order.domain.model.vo;

/**
 * 고객 식별자를 나타내는 값 객체
 */
public record CustomerId(
    Long value
) {
    
    public CustomerId {
        if (value == null) {
            throw new IllegalArgumentException("고객 ID는 null일 수 없습니다.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("고객 ID는 0보다 커야 합니다.");
        }
    }
    
    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }
}