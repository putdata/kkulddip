package com.kkulddip.payment.application.service;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentStatusService {

    private final PaymentRepository paymentRepository;

    public Payment approvePayment(Payment payment, PaymentKey paymentKey, PaymentMethod method, 
        LocalDateTime requestedAt, LocalDateTime approvedAt) {
            
        payment.approve(paymentKey, method, requestedAt, approvedAt);
        return paymentRepository.save(payment);
    }

    public Payment failPayment(Payment payment) {
        payment.fail();
        return paymentRepository.save(payment);
    }

    public Payment cancelPayment(Payment payment, String cancelReason) {
        payment.cancel(cancelReason);
        return paymentRepository.save(payment);
    }
}