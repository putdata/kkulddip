package com.kkulddip.payment.application.service;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentValidationService {

    public void validateTossResponse(Payment payment, TossPaymentResponse tossResponse) {
        // 토스 응답의 orderId는 이제 paymentOrderId와 비교
        if (!payment.getPaymentOrderId().value().equals(tossResponse.orderId())) {
            throw new RuntimeException("토스 응답의 orderId가 일치하지 않습니다. 예상: " + 
                payment.getPaymentOrderId().value() + ", 실제: " + tossResponse.orderId());
        }
    }
}