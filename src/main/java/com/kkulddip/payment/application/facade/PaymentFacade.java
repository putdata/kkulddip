package com.kkulddip.payment.application.facade;

import com.kkulddip.common.lock.DistributedLock;
import com.kkulddip.payment.application.exception.PaymentException;
import com.kkulddip.payment.application.service.PaymentEventService;
import com.kkulddip.payment.application.service.PaymentQueryService;
import com.kkulddip.payment.application.service.PaymentStatusService;
import com.kkulddip.payment.application.service.PaymentValidationService;
import com.kkulddip.payment.application.service.TossApiService;
import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
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
    private final DistributedLock distributedLock;

    @Transactional
    public PaymentResponse confirmPayment(TossPaymentConfirmRequest request) {
        String lockKey = "payment:confirm:" + request.orderId();
        
        if (!distributedLock.tryLock(lockKey)) {
            log.warn("⚠️ 결제 처리 중복 요청 감지: paymentOrderId={}", request.orderId());
            throw PaymentException.alreadyProcessing(request.orderId());
        }

        try {
            // 1. Payment 조회 (request.orderId는 실제로는 paymentOrderId)
            Payment payment = paymentQueryService.findPaymentByPaymentOrderId(request.orderId());

            // READY 상태가 아니면 예외 발생
            if (payment.getStatus() != PaymentStatus.READY) {
                throw PaymentException.invalidPaymentStatus(payment.getStatus().name());
            }
            
            log.info("🔍 Payment 조회: paymentOrderId={}, status={}", request.orderId(), payment.getStatus());

            // 2. Toss API 호출용 요청 생성 (paymentOrderId 사용)
            String tossOrderId = payment.getPaymentOrderId().value();
            
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
                    tossResponse.approvedAt()
                );
                
                // 6. 결제 결과 이벤트 발행
                paymentEventService.publishPaymentResultEvent(updatedPayment);
                
                return PaymentResponse.from(updatedPayment);
                
            } catch (Exception e) {
                log.error("❌ 결제 승인 실패: paymentOrderId={}, error={}", request.orderId(), e.getMessage());
                
                // 실패 처리: Payment 도메인 내에서만 상태 업데이트, Order 이벤트는 발행하지 않음
                // 이렇게 하면 Order는 PAYMENT_PENDING 상태를 유지하여 사용자가 재시도 가능
                paymentStatusService.failPayment(payment);
                
                log.info("결제 실패 처리 완료 - Payment 상태만 업데이트, Order는 재시도 가능한 상태 유지");
                
                throw PaymentException.processingFailed("결제 승인 실패: paymentOrderId=" + request.orderId());
            }
        } finally {
            distributedLock.unlock(lockKey);
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentKey) {
        return paymentQueryService.getPayment(paymentKey);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        return paymentQueryService.getPaymentByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByPaymentOrderId(String paymentOrderId) {
        return paymentQueryService.getPaymentByPaymentOrderId(paymentOrderId);
    }

    /**
     * orderId로 paymentOrderId만 빠르게 조회
     */
    @Transactional(readOnly = true)
    public String getPaymentOrderIdByOrderId(Long orderId) {
        return paymentQueryService.getPaymentOrderIdByOrderId(orderId);
    }

    /**
     * 결제 요청 - orderId로 유효한 PaymentOrderId 반환 (TTL 검증 포함)
     */
    @Transactional
    public String requestPayment(String orderIdStr) {
        try {
            Long orderId = Long.parseLong(orderIdStr);
            String lockKey = "payment:request:" + orderId;
            
            if (!distributedLock.tryLock(lockKey)) {
                log.warn("⚠️ PaymentOrderId 발급 중복 요청 감지: orderId={}", orderId);
                throw PaymentException.alreadyProcessing("orderId=" + orderId);
            }
            
            try {
                return paymentQueryService.getValidPaymentOrderIdByOrderId(orderId);
            } finally {
                distributedLock.unlock(lockKey);
            }
            
        } catch (NumberFormatException e) {
            throw PaymentException.processingFailed("잘못된 주문 ID 형식입니다: " + orderIdStr);
        }
    }

    @Transactional
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