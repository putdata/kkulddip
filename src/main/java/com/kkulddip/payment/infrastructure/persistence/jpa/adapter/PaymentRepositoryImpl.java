package com.kkulddip.payment.infrastructure.persistence.jpa.adapter;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.vo.PaymentOrderId;
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
        Optional<PaymentJpaEntity> existingEntity = paymentJpaRepository.findByOrderId(payment.getOrderId());

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
    public Optional<Payment> findByOrderId(Long orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
            .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentKey(PaymentKey paymentKey) {
        return paymentJpaRepository.findByPaymentKey(paymentKey.value())
            .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentOrderId(PaymentOrderId paymentOrderId) {
        return paymentJpaRepository.findByPaymentOrderId(paymentOrderId.value())
            .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public void delete(Payment payment) {
        paymentJpaRepository.findByOrderId(payment.getOrderId())
            .ifPresent(paymentJpaRepository::delete);
    }

    // order id
    @Override
    public boolean existsByOrderId(Long orderId) {
        return paymentJpaRepository.existsByOrderId(orderId);
    }

    @Override
    public Optional<String> findPaymentOrderIdByOrderId(Long orderId) {
        return paymentJpaRepository.findPaymentOrderIdByOrderId(orderId);
    }
}