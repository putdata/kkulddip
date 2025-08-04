package com.kkulddip.order.domain.model.enums;

public enum OrderStatus {

    // 주문 생성
    CREATED,

    // 결제 대기
    PAYMENT_PENDING,

    // 결제 완료
    PAID,

    // 가게에 주문 확정 요청
    AWAITING_CONFIRMATION,
    
    // 주문 확정
    CONFIRMED,

    // 주문 취소
    CANCELLED,

    // 주문 실패
    FAILED
}