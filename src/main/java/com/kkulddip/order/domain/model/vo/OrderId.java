package com.kkulddip.order.domain.model.vo;

/**
 * 주문 식별자를 나타내는 값 객체
 */
public record OrderId(
    Long value
) {
    
    public OrderId {
        if (value == null) {
            throw new IllegalArgumentException("주문 ID는 null일 수 없습니다.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("주문 ID는 0보다 커야 합니다.");
        }
    }
    
    public static OrderId of(Long value) {
        return new OrderId(value);
    }
}