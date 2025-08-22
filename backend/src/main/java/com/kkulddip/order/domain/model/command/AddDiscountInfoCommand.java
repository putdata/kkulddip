package com.kkulddip.order.domain.model.command;

import com.kkulddip.order.domain.model.enums.DiscountType;
import com.kkulddip.order.domain.model.vo.DiscountCode;
import com.kkulddip.order.domain.model.vo.DiscountInfoId;
import com.kkulddip.order.domain.model.vo.Money;

public record AddDiscountInfoCommand(
    DiscountInfoId discountInfoId,
    DiscountType discountType,
    DiscountCode discountCode,
    Money discountAmount
) {

    public void validate() {
        if (discountType == null) {
            throw new IllegalArgumentException("할인 타입은 필수입니다.");
        }
        if (discountAmount == null) {
            throw new IllegalArgumentException("할인 금액은 필수입니다.");
        }
        if (discountAmount.isLessThan(Money.of(1L))) {
            throw new IllegalArgumentException("할인 금액은 0보다 커야 합니다.");
        }
    }
}
