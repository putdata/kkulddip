package com.kkulddip.payment.domain.repository;

import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.domain.model.vo.PaymentKey;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentKey(PaymentKey paymentKey);
    void delete(Payment payment);
    boolean existsByOrderId(String s);
}