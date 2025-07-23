package com.kkulddip.order.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("대기중", "주문이 접수되어 처리를 대기중입니다"),
    CONFIRMED("확인됨", "주문이 확인되어 처리중입니다"),
    CANCELLED("취소됨", "주문이 취소되었습니다"),
    DELIVERED("배송완료", "주문이 성공적으로 배송완료되었습니다");

    private final String displayName;
    private final String description;

    public boolean canCancel() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean canConfirm() {
        return this == PENDING;
    }

    public boolean canDeliver() {
        return this == CONFIRMED;
    }
} 