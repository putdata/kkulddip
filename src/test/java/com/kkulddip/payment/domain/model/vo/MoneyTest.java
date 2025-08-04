package com.kkulddip.payment.domain.model.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Money 값 객체 테스트")
class MoneyTest {

    @Test
    @DisplayName("Money 생성 - 양수 값으로 Money를 생성할 수 있다")
    void createMoney_PositiveValue_Success() {
        // given
        long value = 1000L;

        // when
        Money money = new Money(value);

        // then
        assertThat(money.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("Money 생성 - 0 값으로 Money를 생성할 수 있다")
    void createMoney_ZeroValue_Success() {
        // given
        long value = 0L;

        // when
        Money money = new Money(value);

        // then
        assertThat(money.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("Money 생성 - 음수 값으로 Money를 생성하면 예외가 발생한다")
    void createMoney_NegativeValue_ThrowsException() {
        // given
        long value = -1000L;

        // when & then
        assertThatThrownBy(() -> new Money(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("금액은 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("정적 팩토리 메서드 - of(long)으로 Money를 생성할 수 있다")
    void of_LongValue_Success() {
        // given
        long value = 5000L;

        // when
        Money money = Money.of(value);

        // then
        assertThat(money.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("정적 팩토리 메서드 - of(String)으로 Money를 생성할 수 있다")
    void of_StringValue_Success() {
        // given
        String value = "7500";

        // when
        Money money = Money.of(value);

        // then
        assertThat(money.value()).isEqualTo(7500L);
    }

    @Test
    @DisplayName("정적 팩토리 메서드 - of(double)으로 Money를 생성할 수 있다")
    void of_DoubleValue_Success() {
        // given
        double value = 12000.0;

        // when
        Money money = Money.of(value);

        // then
        assertThat(money.value()).isEqualTo(12000L);
    }

    @Test
    @DisplayName("덧셈 연산 - 두 Money를 더할 수 있다")
    void add_TwoMoney_Success() {
        // given
        Money money1 = Money.of(1000L);
        Money money2 = Money.of(2000L);

        // when
        Money result = money1.add(money2);

        // then
        assertThat(result.value()).isEqualTo(3000L);
        assertThat(money1.value()).isEqualTo(1000L); // 원본 불변
        assertThat(money2.value()).isEqualTo(2000L); // 원본 불변
    }

    @Test
    @DisplayName("뺄셈 연산 - 두 Money를 뺄 수 있다")
    void subtract_TwoMoney_Success() {
        // given
        Money money1 = Money.of(5000L);
        Money money2 = Money.of(2000L);

        // when
        Money result = money1.subtract(money2);

        // then
        assertThat(result.value()).isEqualTo(3000L);
        assertThat(money1.value()).isEqualTo(5000L); // 원본 불변
        assertThat(money2.value()).isEqualTo(2000L); // 원본 불변
    }

    @Test
    @DisplayName("곱셈 연산 - Money에 정수를 곱할 수 있다")
    void multiply_MoneyAndLong_Success() {
        // given
        Money money = Money.of(1500L);
        long multiplier = 3L;

        // when
        Money result = money.multiply(multiplier);

        // then
        assertThat(result.value()).isEqualTo(4500L);
        assertThat(money.value()).isEqualTo(1500L); // 원본 불변
    }

    @Test
    @DisplayName("비교 연산 - isGreaterThan으로 크기를 비교할 수 있다")
    void isGreaterThan_Comparison_Success() {
        // given
        Money money1 = Money.of(3000L);
        Money money2 = Money.of(2000L);
        Money money3 = Money.of(3000L);

        // when & then
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isGreaterThan(money1)).isFalse();
        assertThat(money1.isGreaterThan(money3)).isFalse();
    }

    @Test
    @DisplayName("비교 연산 - isLessThan으로 크기를 비교할 수 있다")
    void isLessThan_Comparison_Success() {
        // given
        Money money1 = Money.of(2000L);
        Money money2 = Money.of(3000L);
        Money money3 = Money.of(2000L);

        // when & then
        assertThat(money1.isLessThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isFalse();
        assertThat(money1.isLessThan(money3)).isFalse();
    }

    @Test
    @DisplayName("동등성 비교 - equals로 동등성을 비교할 수 있다")
    void equals_Comparison_Success() {
        // given
        Money money1 = Money.of(2500L);
        Money money2 = Money.of(2500L);
        Money money3 = Money.of(3000L);

        // when & then
        assertThat(money1.equals(money2)).isTrue();
        assertThat(money1.equals(money3)).isFalse();
    }

    @Test
    @DisplayName("문자열 변환 - toString으로 문자열로 변환할 수 있다")
    void toString_Conversion_Success() {
        // given
        Money money = Money.of(12345L);

        // when
        String result = money.toString();

        // then
        assertThat(result).isEqualTo("12345");
    }

    @Test
    @DisplayName("Record 동등성 - 같은 값의 Money는 동등하다")
    void recordEquality_SameValue_Equal() {
        // given
        Money money1 = Money.of(1000L);
        Money money2 = Money.of(1000L);

        // when & then
        assertThat(money1).isEqualTo(money2);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
    }

    @Test
    @DisplayName("Record 동등성 - 다른 값의 Money는 동등하지 않다")
    void recordEquality_DifferentValue_NotEqual() {
        // given
        Money money1 = Money.of(1000L);
        Money money2 = Money.of(2000L);

        // when & then
        assertThat(money1).isNotEqualTo(money2);
    }
}