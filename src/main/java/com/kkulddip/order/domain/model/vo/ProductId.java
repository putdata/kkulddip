package com.kkulddip.order.domain.model.vo;

/**
 * 상품 식별자를 나타내는 값 객체
 */
public record ProductId(
    Long value
) {
    
    public ProductId {
        if (value == null) {
            throw new IllegalArgumentException("상품 ID는 null일 수 없습니다.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("상품 ID는 0보다 커야 합니다.");
        }
    }
    
    public static ProductId of(Long value) {
        return new ProductId(value);
    }
}