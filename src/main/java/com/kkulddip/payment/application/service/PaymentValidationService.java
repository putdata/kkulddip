package com.kkulddip.payment.application.service;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentValidationService {

    private final OrderIdConversionService orderIdConversionService;

    public void validateTossResponse(Payment payment, TossPaymentResponse tossResponse) {
        Long responseOrderId = orderIdConversionService.extractTossOrderId(tossResponse.orderId());
        
        if (!payment.getOrderId().equals(responseOrderId)) {
            throw new RuntimeException("토스 응답의 orderId가 일치하지 않습니다. 예상: " + 
                payment.getOrderId() + ", 실제: " + responseOrderId);
        }
    }
}