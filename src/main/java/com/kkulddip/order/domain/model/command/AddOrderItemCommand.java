package com.kkulddip.order.domain.model.command;

import java.util.List;

import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderItemId;
import com.kkulddip.order.domain.model.vo.ProductId;

public record AddOrderItemCommand(
    OrderItemId orderItemId,
    ProductId productId,
    Integer quantity,
    Money unitPrice,
    List<AddDiscountInfoCommand> discountInfos
) {

    public void validate() {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }
        if (unitPrice == null || unitPrice.isLessThan(Money.of(0L))) {
            throw new IllegalArgumentException("단가는 0 이상이어야 합니다.");
        }
    }
}
