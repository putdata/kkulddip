package com.kkulddip.payment.infrastructure.persistence.jpa.adapter;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import com.kkulddip.payment.infrastructure.persistence.jpa.repository.PaymentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentRepositoryImpl 테스트")
class PaymentRepositoryImplTest {

    @Mock
    private PaymentJpaRepository paymentJpaRepository;

    @InjectMocks
    private PaymentRepositoryImpl paymentRepository;

    private Payment testPayment;
    private PaymentJpaEntity testJpaEntity;
    private PaymentKey testPaymentKey;

    @BeforeEach
    void setUp() {
        testPayment = new Payment(
                123L,
                "테스트 주문",
                Money.of(10000),
                "홍길동",
                "test@example.com",
                "http://callback.url",
                "http://fail.url"
        );

        testPaymentKey = PaymentKey.of("payment-key-123");

        testJpaEntity = PaymentJpaEntity.from(testPayment);
    }

    @Test
    @DisplayName("결제 저장 - 새로운 결제를 저장한다")
    void save_NewPayment_Success() {
        // given
        given(paymentJpaRepository.findByOrderId(testPayment.getOrderId())).willReturn(Optional.empty());
        given(paymentJpaRepository.save(any(PaymentJpaEntity.class))).willReturn(testJpaEntity);

        // when
        Payment savedPayment = paymentRepository.save(testPayment);

        // then
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getOrderId()).isEqualTo(testPayment.getOrderId());
        assertThat(savedPayment.getOrderName()).isEqualTo(testPayment.getOrderName());
        assertThat(savedPayment.getAmount()).isEqualTo(testPayment.getAmount());

        then(paymentJpaRepository).should().findByOrderId(testPayment.getOrderId());
        then(paymentJpaRepository).should().save(any(PaymentJpaEntity.class));
    }

    @Test
    @DisplayName("결제 저장 - 기존 결제를 업데이트한다")
    void save_ExistingPayment_UpdatesEntity() {
        // given
        PaymentJpaEntity existingEntity = spy(testJpaEntity);
        given(paymentJpaRepository.findByOrderId(testPayment.getOrderId())).willReturn(Optional.of(existingEntity));
        given(paymentJpaRepository.save(existingEntity)).willReturn(existingEntity);

        // when
        Payment savedPayment = paymentRepository.save(testPayment);

        // then
        assertThat(savedPayment).isNotNull();
        
        then(paymentJpaRepository).should().findByOrderId(testPayment.getOrderId());
        then(existingEntity).should().updateFrom(testPayment);
        then(paymentJpaRepository).should().save(existingEntity);
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 - 결제가 존재하면 반환한다")
    void findByOrderId_ExistingPayment_ReturnsPayment() {
        // given
        Long orderId = 123L;
        given(paymentJpaRepository.findByOrderId(orderId)).willReturn(Optional.of(testJpaEntity));

        // when
        Optional<Payment> result = paymentRepository.findByOrderId(orderId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getOrderId()).isEqualTo(orderId);

        then(paymentJpaRepository).should().findByOrderId(orderId);
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 - 결제가 존재하지 않으면 빈 Optional 반환")
    void findByOrderId_NonExistingPayment_ReturnsEmpty() {
        // given
        Long orderId = 999L;
        given(paymentJpaRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        // when
        Optional<Payment> result = paymentRepository.findByOrderId(orderId);

        // then
        assertThat(result).isEmpty();

        then(paymentJpaRepository).should().findByOrderId(orderId);
    }

    @Test
    @DisplayName("결제키로 결제 조회 - 결제가 존재하면 반환한다")
    void findByPaymentKey_ExistingPayment_ReturnsPayment() {
        // given
        given(paymentJpaRepository.findByPaymentKey(testPaymentKey.value())).willReturn(Optional.of(testJpaEntity));

        // when
        Optional<Payment> result = paymentRepository.findByPaymentKey(testPaymentKey);

        // then
        assertThat(result).isPresent();

        then(paymentJpaRepository).should().findByPaymentKey(testPaymentKey.value());
    }

    @Test
    @DisplayName("결제키로 결제 조회 - 결제가 존재하지 않으면 빈 Optional 반환")
    void findByPaymentKey_NonExistingPayment_ReturnsEmpty() {
        // given
        PaymentKey nonExistingKey = PaymentKey.of("non-existing-key");
        given(paymentJpaRepository.findByPaymentKey(nonExistingKey.value())).willReturn(Optional.empty());

        // when
        Optional<Payment> result = paymentRepository.findByPaymentKey(nonExistingKey);

        // then
        assertThat(result).isEmpty();

        then(paymentJpaRepository).should().findByPaymentKey(nonExistingKey.value());
    }

    @Test
    @DisplayName("결제 삭제 - 존재하는 결제를 삭제한다")
    void delete_ExistingPayment_DeletesEntity() {
        // given
        given(paymentJpaRepository.findByOrderId(testPayment.getOrderId())).willReturn(Optional.of(testJpaEntity));

        // when
        paymentRepository.delete(testPayment);

        // then
        then(paymentJpaRepository).should().findByOrderId(testPayment.getOrderId());
        then(paymentJpaRepository).should().delete(testJpaEntity);
    }

    @Test
    @DisplayName("결제 삭제 - 존재하지 않는 결제는 무시한다")
    void delete_NonExistingPayment_DoesNothing() {
        // given
        given(paymentJpaRepository.findByOrderId(testPayment.getOrderId())).willReturn(Optional.empty());

        // when
        paymentRepository.delete(testPayment);

        // then
        then(paymentJpaRepository).should().findByOrderId(testPayment.getOrderId());
        then(paymentJpaRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("주문 ID 존재 여부 확인 - 존재하면 true 반환")
    void existsByOrderId_ExistingOrderId_ReturnsTrue() {
        // given
        Long orderId = 123L;
        given(paymentJpaRepository.existsByOrderId(orderId)).willReturn(true);

        // when
        boolean result = paymentRepository.existsByOrderId(orderId);

        // then
        assertThat(result).isTrue();

        then(paymentJpaRepository).should().existsByOrderId(orderId);
    }

    @Test
    @DisplayName("주문 ID 존재 여부 확인 - 존재하지 않으면 false 반환")
    void existsByOrderId_NonExistingOrderId_ReturnsFalse() {
        // given
        Long orderId = 999L;
        given(paymentJpaRepository.existsByOrderId(orderId)).willReturn(false);

        // when
        boolean result = paymentRepository.existsByOrderId(orderId);

        // then
        assertThat(result).isFalse();

        then(paymentJpaRepository).should().existsByOrderId(orderId);
    }

    @Test
    @DisplayName("승인된 결제 저장 - 결제키와 승인 정보가 포함된 결제를 저장한다")
    void save_ApprovedPayment_Success() {
        // given
        Payment approvedPayment = new Payment(
                testPaymentKey,
                456L,
                "승인된 주문",
                Money.of(15000),
                "김철수",
                "approved@example.com",
                PaymentStatus.DONE,
                PaymentMethod.CARD,
                OffsetDateTime.parse("2024-02-13T12:17:57+09:00").toLocalDateTime(),
                OffsetDateTime.parse("2024-02-13T12:18:14+09:00").toLocalDateTime(),
                "http://receipt.url",
                "http://callback.url",
                "http://fail.url",
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now()
        );

        PaymentJpaEntity approvedEntity = PaymentJpaEntity.from(approvedPayment);
        given(paymentJpaRepository.findByOrderId(approvedPayment.getOrderId())).willReturn(Optional.empty());
        given(paymentJpaRepository.save(any(PaymentJpaEntity.class))).willReturn(approvedEntity);

        // when
        Payment savedPayment = paymentRepository.save(approvedPayment);

        // then
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getPaymentKey()).isEqualTo(testPaymentKey);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(savedPayment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(savedPayment.isApproved()).isTrue();

        then(paymentJpaRepository).should().save(any(PaymentJpaEntity.class));
    }
}