package com.kkulddip.payment.presentation.event;

import com.kkulddip.common.event.OrderCreatedEvent;
import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.infrastructure.external.toss.utils.TossOrderIdConverter;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final PaymentRepository paymentRepository;

    /**
     * 1. Order 서버로부터 [orderId, amount] 수신하여 Payment 테이블에 저장
     */
    @EventListener
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("📥 주문 생성 이벤트 수신: orderId={}, amount={}, customerId={}",
                event.orderId(), event.amount(), event.customerId());

            // 중복 처리 방지
            if (paymentRepository.existsByOrderId(event.orderId())) {
                log.warn("⚠️ 이미 처리된 주문: orderId={}", event.orderId());
                return;
            }

            // Payment 엔티티 생성 (READY 상태)
            // [orderId, amount, customerId] 사용하여 생성
            Payment payment = new Payment(
                event.orderId(),           // orderId
                "띱박스 결제",                // orderName (기본값)
                Money.of(event.amount()), // amount
                event.customerId()         // customerId
            );

            // DB 저장
            Payment savedPayment = paymentRepository.save(payment);

            log.info("✅ Payment 저장 완료: orderId={}, paymentOrderId={}, customerId={}, amount={}, status={}",
                event.orderId(), savedPayment.getPaymentOrderId().value(), event.customerId(), event.amount(), PaymentStatus.READY);

        } catch (Exception e) {
            log.error("❌ Payment 저장 실패: orderId={}, error={}",
                event.orderId(), e.getMessage(), e);
            throw e;
        }
    }
}