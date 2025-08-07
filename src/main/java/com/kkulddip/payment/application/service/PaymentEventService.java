package com.kkulddip.payment.application.service;

import com.kkulddip.common.event.PaymentResultEvent;
import com.kkulddip.payment.domain.model.aggregate.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventService {

    private final ApplicationEventPublisher eventPublisher;

    public void publishPaymentResultEvent(Payment payment) {
        PaymentResultEvent resultEvent = new PaymentResultEvent(
            payment.getOrderId(),
            payment.getStatus().name()
        );

        eventPublisher.publishEvent(resultEvent);
        log.info("📤 결제 결과 이벤트 발행: orderId={}, status={}",
            resultEvent.orderId(), resultEvent.status());
    }
}