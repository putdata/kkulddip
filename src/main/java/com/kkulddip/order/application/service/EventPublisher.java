package com.kkulddip.order.application.service;

import com.kkulddip.common.event.OrderCreatedEvent;
import com.kkulddip.common.event.PaymentResultEvent;

/**
 * 이벤트 발행 인터페이스
 * 환경에 따라 Redis Pub/Sub 또는 Spring Event 사용
 */
public interface EventPublisher {
    
    /**
     * 결제 완료 이벤트 발행
     */
    void publishPaymentCompleted(PaymentResultEvent event);
    
    /**
     * 주문 생성 이벤트 발행
     */
    void publishOrderCreated(OrderCreatedEvent event);
}