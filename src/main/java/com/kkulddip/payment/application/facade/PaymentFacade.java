package com.kkulddip.payment.application.facade;

import com.kkulddip.common.event.PaymentResultEvent;
import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.infrastructure.external.toss.TossPaymentsApiClient;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentConfirmRequest;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import com.kkulddip.payment.interfaces.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class PaymentFacade {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsApiClient tossPaymentsApiClient;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse confirmPayment(TossPaymentConfirmRequest request) {

        // 1. DB에서 orderId로 Payment 조회
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new RuntimeException(
                "결제 정보를 찾을 수 없습니다: " + request.orderId()));

        log.info("🔍 Payment 조회: orderId={}, status={}",
            request.orderId(), payment.getStatus());

        // 2. Toss API 호출을 위한 요청 생성 [paymentKey, orderId, amount]
        TossPaymentConfirmRequest tossRequest = new TossPaymentConfirmRequest(
            request.paymentKey(),
            request.orderId(),
            payment.getAmount().value()  // DB에서 조회한 amount
        );

        try {
            // 3. Toss API 호출
            log.info("🚀 Toss API 호출: paymentKey={}, orderId={}, amount={}",
                tossRequest.paymentKey(), tossRequest.orderId(), tossRequest.amount());

            TossPaymentResponse tossResponse = tossPaymentsApiClient.confirmPayment(
                tossRequest.paymentKey(), tossRequest.orderId(), tossRequest.amount());

            log.info("✅ Toss API 성공: status={}", tossResponse.status());

            // 4. Payment 엔티티 업데이트 (성공)
            payment.approve(
                PaymentKey.of(tossResponse.paymentKey()),
                PaymentMethod.fromString(tossResponse.method()),
                tossResponse.requestedAt(),
                tossResponse.approvedAt(),
                tossResponse.getReceiptUrl()
            );

            paymentRepository.save(payment);

            // 5. 🔥 Order 서버로 결과 이벤트 발행: [orderId, status]
            PaymentResultEvent resultEvent = new PaymentResultEvent(
                payment.getOrderId(),
                payment.getStatus().name()  // "DONE"
            );

            eventPublisher.publishEvent(resultEvent);
            log.info("📤 결제 결과 이벤트 발행: orderId={}, status={}",
                resultEvent.orderId(), resultEvent.status());

            return PaymentResponse.from(payment);

        } catch (Exception e) {
            log.error("❌ Toss API 실패: orderId={}, error={}",
                request.orderId(), e.getMessage());

            // 실패 처리
            payment.fail();
            paymentRepository.save(payment);

            // 🔥 실패 이벤트 발행: [orderId, status]
            PaymentResultEvent failEvent = new PaymentResultEvent(
                payment.getOrderId(),
                payment.getStatus().name()  // "ABORTED"
            );

            eventPublisher.publishEvent(failEvent);
            log.info("📤 결제 실패 이벤트 발행: orderId={}, status={}",
                failEvent.orderId(), failEvent.status());

            throw new RuntimeException("결제 처리 실패: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + orderId));

        return PaymentResponse.from(payment);
    }

    public PaymentResponse cancelPayment(String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        // 토스페이먼츠 취소 API 호출
        TossPaymentResponse tossResponse = tossPaymentsApiClient.cancelPayment(paymentKey, cancelReason);

        // 결제 취소 처리
        payment.cancel(cancelReason);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("결제 취소 완료: paymentKey={}", paymentKey);

        return PaymentResponse.from(savedPayment);
    }
}