package com.kkulddip.payment.domain.service;

import com.kkulddip.payment.domain.model.aggregate.Payment;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentDomainService 테스트")
class PaymentDomainServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentDomainService paymentDomainService;

    private Payment testPayment;
    private PaymentKey testPaymentKey;

    @BeforeEach
    void setUp() {
        testPayment = new Payment(
                123L,
                "테스트 주문",
                Money.of(10000),
                1L
        );
        testPaymentKey = PaymentKey.of("payment-key-123");
    }

    @Test
    @DisplayName("결제 금액 검증 - 동일한 금액이면 검증 통과")
    void validatePaymentAmount_SameAmount_Success() {
        // given
        Money requestAmount = Money.of(10000);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> paymentDomainService.validatePaymentAmount(testPayment, requestAmount));
    }

    @Test
    @DisplayName("결제 금액 검증 - 다른 금액이면 예외 발생")
    void validatePaymentAmount_DifferentAmount_ThrowsException() {
        // given
        Money requestAmount = Money.of(20000);

        // when & then
        assertThatThrownBy(() -> paymentDomainService.validatePaymentAmount(testPayment, requestAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("결제 금액 검증 - 0원 결제도 검증 가능")
    void validatePaymentAmount_ZeroAmount_Success() {
        // given
        Payment zeroPayment = new Payment(
                0L,
                "무료 주문",
                Money.of(0),
                1L
        );
        Money requestAmount = Money.of(0);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> paymentDomainService.validatePaymentAmount(zeroPayment, requestAmount));
    }

    @Test
    @DisplayName("결제키 유일성 검증 - 존재하지 않는 결제키면 검증 통과")
    void validatePaymentKeyUnique_NonExistentKey_Success() {
        // given
        given(paymentRepository.findByPaymentKey(testPaymentKey)).willReturn(Optional.empty());

        // when & then
        assertThatNoException()
                .isThrownBy(() -> paymentDomainService.validatePaymentKeyUnique(testPaymentKey));

        then(paymentRepository).should().findByPaymentKey(testPaymentKey);
    }

    @Test
    @DisplayName("결제키 유일성 검증 - 이미 존재하는 결제키면 예외 발생")
    void validatePaymentKeyUnique_ExistentKey_ThrowsException() {
        // given
        given(paymentRepository.findByPaymentKey(testPaymentKey)).willReturn(Optional.of(testPayment));

        // when & then
        assertThatThrownBy(() -> paymentDomainService.validatePaymentKeyUnique(testPaymentKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 결제키입니다: " + testPaymentKey.value());

        then(paymentRepository).should().findByPaymentKey(testPaymentKey);
    }

    @Test
    @DisplayName("결제키 유일성 검증 - 서로 다른 결제키는 모두 유일성 검증 통과")
    void validatePaymentKeyUnique_DifferentKeys_AllSuccess() {
        // given
        PaymentKey key1 = PaymentKey.of("payment-key-1");
        PaymentKey key2 = PaymentKey.of("payment-key-2");
        PaymentKey key3 = PaymentKey.of("payment-key-3");

        given(paymentRepository.findByPaymentKey(any(PaymentKey.class))).willReturn(Optional.empty());

        // when & then
        assertThatNoException().isThrownBy(() -> {
            paymentDomainService.validatePaymentKeyUnique(key1);
            paymentDomainService.validatePaymentKeyUnique(key2);
            paymentDomainService.validatePaymentKeyUnique(key3);
        });

        then(paymentRepository).should(times(3)).findByPaymentKey(any(PaymentKey.class));
    }

    @Test
    @DisplayName("복합 검증 - 금액과 결제키를 함께 검증할 수 있다")
    void validateBoth_AmountAndPaymentKey_Success() {
        // given
        Money requestAmount = Money.of(10000);
        PaymentKey uniquePaymentKey = PaymentKey.of("unique-payment-key");

        given(paymentRepository.findByPaymentKey(uniquePaymentKey)).willReturn(Optional.empty());

        // when & then
        assertThatNoException().isThrownBy(() -> {
            paymentDomainService.validatePaymentAmount(testPayment, requestAmount);
            paymentDomainService.validatePaymentKeyUnique(uniquePaymentKey);
        });

        then(paymentRepository).should().findByPaymentKey(uniquePaymentKey);
    }

    @Test
    @DisplayName("복합 검증 실패 - 금액이 다르고 결제키가 중복되면 모두 예외 발생")
    void validateBoth_AmountAndPaymentKey_BothFail() {
        // given
        Money wrongAmount = Money.of(20000);
        PaymentKey duplicatePaymentKey = PaymentKey.of("duplicate-payment-key");

        given(paymentRepository.findByPaymentKey(duplicatePaymentKey)).willReturn(Optional.of(testPayment));

        // when & then
        assertThatThrownBy(() -> paymentDomainService.validatePaymentAmount(testPayment, wrongAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액이 일치하지 않습니다.");

        assertThatThrownBy(() -> paymentDomainService.validatePaymentKeyUnique(duplicatePaymentKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 결제키입니다: " + duplicatePaymentKey.value());
    }
}