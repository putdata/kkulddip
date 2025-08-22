package com.kkulddip.payment.application.service;

import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.payment.application.exception.PaymentApiException;
import com.kkulddip.payment.infrastructure.external.toss.TossPaymentsApiClient;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TossApiService {

    private final TossPaymentsApiClient tossPaymentsApiClient;

    public TossPaymentResponse confirmPayment(String paymentKey, String orderId, Long amount) {
        log.info("🚀 Toss API 호출: paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);

        try {
            TossPaymentResponse response = tossPaymentsApiClient.confirmPayment(paymentKey, orderId, amount);
            log.info("✅ Toss API 성공: status={}", response.status());

            return response;
        } catch (Exception e) {
            log.error("❌ Toss API 실패: paymentKey={}, orderId={}, amount={}, error={}", paymentKey, orderId, amount, e.getMessage());
            throw new PaymentApiException(ErrorCode.PAYMENT_API_ERROR);
        }
    }

    public TossPaymentResponse cancelPayment(String paymentKey, String cancelReason) {
        log.info("🚀 Toss 취소 API 호출: paymentKey={}, reason={}", paymentKey, cancelReason);
        
        try {
            TossPaymentResponse response = tossPaymentsApiClient.cancelPayment(paymentKey, cancelReason);    
            log.info("✅ Toss 취소 API 성공: status={}", response.status());
            
        return response;
        } catch (Exception e) {
            log.error("❌ Toss API 실패: paymentKey={}, reason={}, error={}", paymentKey, cancelReason, e.getMessage());
            throw new PaymentApiException(ErrorCode.PAYMENT_API_ERROR);
        }
    }
}