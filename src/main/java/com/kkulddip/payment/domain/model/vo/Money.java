package com.kkulddip.payment.domain.model.vo;

/**
 * 금액을 나타내는 값 객체
 */
public record Money(
    long value
) {
    
    public Money {
        if (value < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
        }
    }
    
    public static Money of(long value) {
        return new Money(value);
    }
    
    public static Money of(String value) {
        return new Money(Long.parseLong(value));
    }
    
    public static Money of(double value) {
        return new Money((long) value);
    }

    public Money add(Money other) {
        return new Money(this.value + other.value);
    }

    public Money subtract(Money other) {
        return new Money(this.value - other.value);
    }

    public Money multiply(long multiplier) {
        return new Money(this.value * multiplier);
    }

    public boolean isGreaterThan(Money other) {
        return this.value > other.value;
    }

    public boolean isLessThan(Money other) {
        return this.value < other.value;
    }

    public boolean equals(Money other) {
        return this.value == other.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}