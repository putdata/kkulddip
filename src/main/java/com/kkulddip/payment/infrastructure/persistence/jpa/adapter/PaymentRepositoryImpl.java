package com.kkulddip.payment.infrastructure.persistence.jpa.adapter;

import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import com.kkulddip.payment.infrastructure.persistence.jpa.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        Optional<PaymentJpaEntity> existingEntity =
                paymentJpaRepository.findByOrderId(payment.getOrderId());

        if (existingEntity.isPresent()) {
            PaymentJpaEntity entity = existingEntity.get();
            entity.updateFrom(payment);
            return paymentJpaRepository.save(entity).toDomain();
        } else {
            PaymentJpaEntity entity = PaymentJpaEntity.from(payment);
            return paymentJpaRepository.save(entity).toDomain();
        }
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentKey(PaymentKey paymentKey) {
        return paymentJpaRepository.findByPaymentKey(paymentKey.value())
                .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public void delete(Payment payment) {
        paymentJpaRepository.findByOrderId(payment.getOrderId())
                .ifPresent(paymentJpaRepository::delete);
    }

    // order id
    @Override
    public boolean existsByOrderId(String s) {
        return paymentJpaRepository.existsByOrderId(s);
    }
}