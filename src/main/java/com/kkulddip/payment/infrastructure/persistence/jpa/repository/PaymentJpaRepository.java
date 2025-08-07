package com.kkulddip.payment.infrastructure.persistence.jpa.repository;

import com.kkulddip.payment.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    Optional<PaymentJpaEntity> findByOrderId(Long orderId);
    Optional<PaymentJpaEntity> findByPaymentKey(String paymentKey);
    Optional<PaymentJpaEntity> findByPaymentOrderId(String paymentOrderId);
    boolean existsByOrderId(Long orderId);
    
    @Query("SELECT p.paymentOrderId FROM PaymentJpaEntity p WHERE p.orderId = :orderId")
    Optional<String> findPaymentOrderIdByOrderId(@Param("orderId") Long orderId);
}