package com.kkulddip.order.domain.model.vo;

/**
 * 주문 아이템 식별자를 나타내는 값 객체
 */
public record OrderItemId(
    Long value
) {
    
    public OrderItemId {
        // JPA value 자동 주입 고려: null 허용
    }
    
    public static OrderItemId of(Long value) {
        return new OrderItemId(value);
    }
}