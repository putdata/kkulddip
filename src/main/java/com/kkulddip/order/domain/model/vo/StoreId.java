package com.kkulddip.order.domain.model.vo;

/**
 * 매장 식별자를 나타내는 값 객체
 */
public record StoreId(
    Long value
) {
    
    public StoreId {
        if (value == null) {
            throw new IllegalArgumentException("매장 ID는 null일 수 없습니다.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("매장 ID는 0보다 커야 합니다.");
        }
    }
    
    public static StoreId of(Long value) {
        return new StoreId(value);
    }
}