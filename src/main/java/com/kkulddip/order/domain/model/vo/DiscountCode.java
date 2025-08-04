package com.kkulddip.order.domain.model.vo;

/**
 * 할인 코드를 나타내는 값 객체
 */
public record DiscountCode(
    Long code
) {

    public DiscountCode {
        if (code == null) {
            throw new IllegalArgumentException("할인 코드는 null일 수 없습니다.");
        }
        if (code <= 0) {
            throw new IllegalArgumentException("할인 코드는 0보다 커야 합니다.");
        }
    }


    public static DiscountCode of(Long code) {
        return new DiscountCode(code);
    }
}
