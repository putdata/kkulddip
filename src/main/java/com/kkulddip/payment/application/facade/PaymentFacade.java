package com.kkulddip.payment.application.facade;

import com.kkulddip.payment.application.service.OrderIdConversionService;
import com.kkulddip.payment.application.service.PaymentEventService;
import com.kkulddip.payment.application.service.PaymentQueryService;
import com.kkulddip.payment.application.service.PaymentStatusService;
import com.kkulddip.payment.application.service.PaymentValidationService;
import com.kkulddip.payment.application.service.TossApiService;
import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentConfirmRequest;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import com.kkulddip.payment.presentation.dto.response.PaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class PaymentFacade {

    private final PaymentQueryService paymentQueryService;
    private final TossApiService tossApiService;
    private final PaymentValidationService paymentValidationService;
    private final PaymentStatusService paymentStatusService;
    private final PaymentEventService paymentEventService;
    private final OrderIdConversionService orderIdConversionService;

    @Transactional
    public PaymentResponse confirmPayment(TossPaymentConfirmRequest request) {
        // 1. Payment 조회
        Payment payment = paymentQueryService.findPaymentByOrderId(request.orderId());
        
        log.info("🔍 Payment 조회: orderId={}, status={}", request.orderId(), payment.getStatus());

        // 2. Toss API 호출용 요청 생성
        String tossOrderId = orderIdConversionService.toTossOrderId(payment.getOrderId());
        
        try {
            // 3. Toss API 호출
            TossPaymentResponse tossResponse = tossApiService.confirmPayment(
                request.paymentKey(), tossOrderId, payment.getAmount().value());
            
            // 4. 응답 검증
            paymentValidationService.validateTossResponse(payment, tossResponse);
            
            // 5. Payment 상태 업데이트 (승인)
            Payment updatedPayment = paymentStatusService.approvePayment(
                payment,
                PaymentKey.of(tossResponse.paymentKey()),
                PaymentMethod.fromString(tossResponse.method()),
                tossResponse.requestedAt(),
                tossResponse.approvedAt(),
                tossResponse.getReceiptUrl()
            );
            
            // 6. 결제 결과 이벤트 발행
            paymentEventService.publishPaymentResultEvent(updatedPayment);
            
            return PaymentResponse.from(updatedPayment);
            
        } catch (Exception e) {
            log.error("❌ 결제 승인 실패: orderId={}, error={}", request.orderId(), e.getMessage());
            
            // 실패 처리: 상태 업데이트 + 이벤트 발행
            Payment failedPayment = paymentStatusService.failPayment(payment);
            paymentEventService.publishPaymentResultEvent(failedPayment);
            
            throw new RuntimeException("결제 처리 실패: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentKey) {
        return paymentQueryService.getPayment(paymentKey);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        return paymentQueryService.getPaymentByOrderId(orderId);
    }

    public PaymentResponse cancelPayment(String paymentKey, String cancelReason) {
        // 1. Payment 조회
        Payment payment = paymentQueryService.findPaymentByPaymentKey(paymentKey);
        
        // 2. Toss 취소 API 호출
        TossPaymentResponse tossResponse = tossApiService.cancelPayment(paymentKey, cancelReason);
        
        // 3. Payment 상태 업데이트 (취소)
        Payment canceledPayment = paymentStatusService.cancelPayment(payment, cancelReason);
        
        log.info("결제 취소 완료: paymentKey={}", paymentKey);
        
        return PaymentResponse.from(canceledPayment);
    }
}