package com.kkulddip.payment.domain.repository;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.vo.PaymentOrderId;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByPaymentKey(PaymentKey paymentKey);
    Optional<Payment> findByPaymentOrderId(PaymentOrderId paymentOrderId);
    void delete(Payment payment);
    boolean existsByOrderId(Long orderId);
    Optional<String> findPaymentOrderIdByOrderId(Long orderId);
}