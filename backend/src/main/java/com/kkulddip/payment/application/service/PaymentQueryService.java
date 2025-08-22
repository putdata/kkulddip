package com.kkulddip.payment.application.service;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.vo.PaymentOrderId;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import com.kkulddip.payment.presentation.dto.response.PaymentResponse;
import com.kkulddip.payment.application.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse getPayment(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        return PaymentResponse.from(payment);
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + orderId));

        return PaymentResponse.from(payment);
    }

    public Payment findPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + orderId));
    }

    public Payment findPaymentByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentKey));
    }

    public PaymentResponse getPaymentByPaymentOrderId(String paymentOrderId) {
        Payment payment = paymentRepository.findByPaymentOrderId(PaymentOrderId.of(paymentOrderId))
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentOrderId));

        return PaymentResponse.from(payment);
    }

    public Payment findPaymentByPaymentOrderId(String paymentOrderId) {
        return paymentRepository.findByPaymentOrderId(PaymentOrderId.of(paymentOrderId))
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentOrderId));
    }

    /**
     * orderId로 paymentOrderId만 빠르게 조회
     */
    public String getPaymentOrderIdByOrderId(Long orderId) {
        return paymentRepository.findPaymentOrderIdByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + orderId));
    }

    /**
     * orderId로 TTL을 검증하여 유효한 PaymentOrderId 반환
     * READY, ABORTED, EXPIRED 상태에서 진행 가능, TTL 만료 시 새로운 PaymentOrderId 생성
     */
    @Transactional
    public String getValidPaymentOrderIdByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> PaymentException.orderNotFound(orderId));

        // 재시도 가능한 상태인지 확인
        if (!canRetryPayment(payment.getStatus())) {
            throw PaymentException.invalidPaymentStatus(
                "결제 재시도가 불가능한 상태입니다. 현재 상태: " + payment.getStatus()
            );
        }

        // ABORTED나 EXPIRED 상태인 경우 무조건 새로운 PaymentOrderId 생성 후 READY 상태로 변경
        if (payment.getStatus() == PaymentStatus.ABORTED || payment.getStatus() == PaymentStatus.EXPIRED) {
            payment.regenerateForRetry();  // 새로운 PaymentOrderId 생성 + READY 상태 변경
        }
        
        // TTL 검증 후 유효한 PaymentOrderId 반환
        String validPaymentOrderId = payment.getValidPaymentOrderId();
        
        // PaymentOrderId가 재생성된 경우 저장
        paymentRepository.save(payment);
        
        return validPaymentOrderId;
    }
    
    /**
     * 결제 재시도가 가능한 상태인지 확인
     */
    private boolean canRetryPayment(PaymentStatus status) {
        return switch (status) {
            case READY -> true;        // 기본 상태
            case ABORTED -> true;      // 결제 실패 후 재시도
            case EXPIRED -> true;      // 만료 후 재시도
            case DONE -> false;        // 이미 완료된 결제
            case CANCELED -> false;    // 이미 결제 완료 후 취소된 상태
            case PARTIAL_CANCELED -> false; // 이미 결제 완료 후 부분 취소된 상태
            case IN_PROGRESS -> false; // 진행 중인 결제
            case WAITING_FOR_DEPOSIT -> false; // 입금 대기 중
        };
    }
}