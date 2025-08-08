package com.kkulddip.order.presentation.rest.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderItemRequest(
    @NotNull Long productId,
    @NotNull Integer quantity,
    @NotNull Integer unitPrice,
    @Valid List<DiscountInfoRequest> discountInfos
) {
    
    /**
     * 주문 아이템의 총 가격을 계산합니다 (임시 구현).
     * Store 도메인 구현 후 실제 상품 가격 조회로 대체될 예정입니다.
     * 
     * @return quantity × unitPrice
     */
    public Integer totalPrice() {
        if (quantity == null || unitPrice == null) {
            throw new IllegalArgumentException("수량과 단가는 null일 수 없습니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("단가는 0 이상이어야 합니다.");
        }
        return quantity * unitPrice;
    }
}