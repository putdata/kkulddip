package com.kkulddip.payment.domain.service;

import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentRepository paymentRepository;

    public void validatePaymentAmount(Payment payment, Money requestAmount) {
        if (!payment.getAmount().equals(requestAmount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

    public void validatePaymentKeyUnique(PaymentKey paymentKey) {
        if (paymentRepository.findByPaymentKey(paymentKey).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 결제키입니다: " + paymentKey.value());
        }
    }
}