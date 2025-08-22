package com.kkulddip.order.domain.model.vo;

/**
 * 주문 유닛 상세정보 식별자를 나타내는 값 객체
 */
public record DiscountInfoId(
    Long value
) {
    
    public DiscountInfoId {
        // JPA value 자동 주입 고려: null 허용
    }
    
    public static DiscountInfoId of(Long value) {
        return new DiscountInfoId(value);
    }
}