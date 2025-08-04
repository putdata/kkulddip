package com.kkulddip.payment.domain.model.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PaymentKey 값 객체 테스트")
class PaymentKeyTest {

    @Test
    @DisplayName("PaymentKey 생성 - 유효한 값으로 PaymentKey를 생성할 수 있다")
    void createPaymentKey_ValidValue_Success() {
        // given
        String value = "payment-key-123";

        // when
        PaymentKey paymentKey = new PaymentKey(value);

        // then
        assertThat(paymentKey.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("PaymentKey 생성 - null 값으로 PaymentKey를 생성하면 예외가 발생한다")
    void createPaymentKey_NullValue_ThrowsException() {
        // given
        String value = null;

        // when & then
        assertThatThrownBy(() -> new PaymentKey(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제키는 필수입니다.");
    }

    @Test
    @DisplayName("PaymentKey 생성 - 빈 문자열로 PaymentKey를 생성하면 예외가 발생한다")
    void createPaymentKey_EmptyValue_ThrowsException() {
        // given
        String value = "";

        // when & then
        assertThatThrownBy(() -> new PaymentKey(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제키는 필수입니다.");
    }

    @Test
    @DisplayName("PaymentKey 생성 - 공백만 있는 문자열로 PaymentKey를 생성하면 예외가 발생한다")
    void createPaymentKey_BlankValue_ThrowsException() {
        // given
        String value = "   ";

        // when & then
        assertThatThrownBy(() -> new PaymentKey(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제키는 필수입니다.");
    }

    @Test
    @DisplayName("정적 팩토리 메서드 - of 메서드로 PaymentKey를 생성할 수 있다")
    void of_ValidValue_Success() {
        // given
        String value = "payment-key-456";

        // when
        PaymentKey paymentKey = PaymentKey.of(value);

        // then
        assertThat(paymentKey.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("문자열 변환 - toString으로 문자열로 변환할 수 있다")
    void toString_Conversion_Success() {
        // given
        String value = "payment-key-789";
        PaymentKey paymentKey = PaymentKey.of(value);

        // when
        String result = paymentKey.toString();

        // then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("Record 동등성 - 같은 값의 PaymentKey는 동등하다")
    void recordEquality_SameValue_Equal() {
        // given
        String value = "payment-key-equal";
        PaymentKey paymentKey1 = PaymentKey.of(value);
        PaymentKey paymentKey2 = PaymentKey.of(value);

        // when & then
        assertThat(paymentKey1).isEqualTo(paymentKey2);
        assertThat(paymentKey1.hashCode()).isEqualTo(paymentKey2.hashCode());
    }

    @Test
    @DisplayName("Record 동등성 - 다른 값의 PaymentKey는 동등하지 않다")
    void recordEquality_DifferentValue_NotEqual() {
        // given
        PaymentKey paymentKey1 = PaymentKey.of("payment-key-1");
        PaymentKey paymentKey2 = PaymentKey.of("payment-key-2");

        // when & then
        assertThat(paymentKey1).isNotEqualTo(paymentKey2);
    }

    @Test
    @DisplayName("공백 처리 - 앞뒤 공백이 있는 값으로 PaymentKey를 생성하면 trim 후 검증한다")
    void createPaymentKey_ValueWithSpaces_TrimsBeforeValidation() {
        // given
        String valueWithSpaces = " payment-key-with-spaces ";

        // when
        PaymentKey paymentKey = PaymentKey.of(valueWithSpaces);

        // then
        assertThat(paymentKey.value()).isEqualTo(valueWithSpaces); // 원본 값 유지
    }
}