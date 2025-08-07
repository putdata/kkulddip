package com.kkulddip.payment.application.service;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import com.kkulddip.payment.presentation.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;
    private final OrderIdConversionService orderIdConversionService;

    public PaymentResponse getPayment(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        return PaymentResponse.from(payment);
    }

    public PaymentResponse getPaymentByOrderId(String orderId) {
        Long orderIdLong = orderIdConversionService.extractTossOrderId(orderId);
        Payment payment = paymentRepository.findByOrderId(orderIdLong)
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + orderId));

        return PaymentResponse.from(payment);
    }

    public Payment findPaymentByOrderId(String orderId) {
        Long orderIdLong = orderIdConversionService.extractTossOrderId(orderId);
        return paymentRepository.findByOrderId(orderIdLong)
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + orderId));
    }

    public Payment findPaymentByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
            .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentKey));
    }
}