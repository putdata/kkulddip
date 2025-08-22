package com.kkulddip.order.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.kkulddip.order.application.service.EventPublisher;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.common.event.OrderCreatedEvent;
import com.kkulddip.common.event.PaymentResultEvent;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishPaymentCompleted(PaymentResultEvent event) {
        try {
            log.info("결제 완료 이벤트 발행 - orderId: {}", event.orderId());
            applicationEventPublisher.publishEvent(event);
            log.debug("Spring ApplicationEvent로 PaymentResultEvent 발행 완료");
        } catch (Exception e) {
            log.error("결제 완료 이벤트 발행 실패 - orderId: {}, operation: publishPaymentCompleted, details: {}", 
                event.orderId(), e.getMessage(), e);
            throw OrderException.orderEventPublishFailed(e);
        }
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("주문 생성 이벤트 발행 - orderId: {}, amount: {}", event.orderId(), event.amount());
            applicationEventPublisher.publishEvent(event);
            log.debug("Spring ApplicationEvent로 OrderCreatedEvent 발행 완료");
        } catch (Exception e) {
            log.error("주문 생성 이벤트 발행 실패 - orderId: {}, amount: {}, operation: publishOrderCreated, details: {}", 
                event.orderId(), event.amount(), e.getMessage(), e);
            throw OrderException.orderEventPublishFailed(e);
        }
    }
}